package org.dromara.e2e.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.PageResult;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.oss.client.OssClient;
import org.dromara.common.oss.factory.OssFactory;
import org.dromara.common.oss.model.Options;
import org.dromara.common.oss.model.PutObjectResult;
import org.dromara.e2e.config.E2eProperties;
import org.dromara.e2e.domain.E2eTestArtifact;
import org.dromara.e2e.domain.E2eTestCase;
import org.dromara.e2e.domain.E2eTestTask;
import org.dromara.e2e.domain.E2eTestTaskCase;
import org.dromara.e2e.domain.bo.E2eTestTaskBo;
import org.dromara.e2e.domain.vo.E2eTestTaskVo;
import org.dromara.e2e.mapper.E2eTestArtifactMapper;
import org.dromara.e2e.mapper.E2eTestCaseMapper;
import org.dromara.e2e.mapper.E2eTestTaskCaseMapper;
import org.dromara.e2e.mapper.E2eTestTaskMapper;
import org.dromara.e2e.manager.E2eSseManager;
import org.dromara.e2e.service.IE2eTestTaskService;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * E2E测试任务 服务层处理
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class E2eTestTaskServiceImpl implements IE2eTestTaskService {

    private final E2eTestTaskMapper testTaskMapper;
    private final E2eTestTaskCaseMapper testTaskCaseMapper;
    private final E2eTestCaseMapper testCaseMapper;
    private final E2eTestArtifactMapper testArtifactMapper;
    private final E2eProperties e2eProperties;
    private final ApplicationContext applicationContext;
    private final E2eSseManager e2eSseManager;

    private static final ConcurrentHashMap<Long, List<String>> LOG_BUFFER = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, Process> RUNNING_PROCESSES = new ConcurrentHashMap<>();

    /**
     * 推送日志到缓冲区 + SSE
     */
    private void pushLog(Long taskId, List<String> logLines, String message) {
        logLines.add(message);
        e2eSseManager.send(taskId, message);
    }

    @Override
    public PageResult<E2eTestTaskVo> selectPageTaskList(E2eTestTaskBo bo, PageQuery pageQuery) {
        Page<E2eTestTaskVo> page = testTaskMapper.selectVoPage(pageQuery.build(), buildQueryWrapper(bo));
        return PageResult.build(page.getRecords(), page.getTotal());
    }

    @Override
    public E2eTestTaskVo selectTaskById(Long taskId) {
        return testTaskMapper.selectVoById(taskId);
    }

    @Override
    public Long createAndExecuteTask(E2eTestTaskBo bo) {
        E2eTestTask task = MapstructUtils.convert(bo, E2eTestTask.class);
        task.setStatus("0");
        task.setTotalCases(bo.getCaseIds() != null ? bo.getCaseIds().size() : 0);
        task.setPassedCases(0);
        task.setFailedCases(0);
        task.setSkippedCases(0);
        testTaskMapper.insert(task);

        Long taskId = task.getTaskId();

        if (bo.getCaseIds() != null && !bo.getCaseIds().isEmpty()) {
            for (Long caseId : bo.getCaseIds()) {
                E2eTestTaskCase taskCase = new E2eTestTaskCase();
                taskCase.setTaskId(taskId);
                taskCase.setCaseId(caseId);
                testTaskCaseMapper.insert(taskCase);
            }
        }

        // 通过代理调用，确保@Async生效
        applicationContext.getBean(IE2eTestTaskService.class)
            .executeTaskAsync(taskId, bo.getBrowser(), bo.getHeaded(), bo.getCaseIds());

        return taskId;
    }

    @Override
    @Async("e2eTaskExecutor")
    public void executeTaskAsync(Long taskId, String browser, String headed, List<Long> caseIds) {
        String basePath = e2eProperties.getResolvedBasePath();
        List<String> logLines = new ArrayList<>();
        LOG_BUFFER.put(taskId, logLines);

        Path resultJsonPath = Paths.get(basePath, "test-results", "results.json");
        Path reportDir = Paths.get(basePath, "playwright-report");

        try {
            // 更新状态为执行中
            E2eTestTask running = new E2eTestTask();
            running.setTaskId(taskId);
            running.setStatus("1");
            running.setStartTime(LocalDateTime.now());
            testTaskMapper.updateById(running);

            // 将DB中的用例内容写入spec文件，确保执行时文件内容是最新的
            List<String> tempFiles = new ArrayList<>();
            if (caseIds != null && !caseIds.isEmpty()) {
                List<E2eTestCase> cases = testCaseMapper.selectBatchIds(caseIds);
                for (E2eTestCase tc : cases) {
                    if (tc.getContent() != null && !tc.getContent().isEmpty()) {
                        String resolvedBase = e2eProperties.getResolvedBasePath();
                        Path filePath = Paths.get(resolvedBase, tc.getSpecFile());
                        Files.createDirectories(filePath.getParent());
                        Files.writeString(filePath, tc.getContent(), StandardCharsets.UTF_8);
                        tempFiles.add(filePath.toString());
                        log.debug("已将用例内容写入文件: {}", filePath);
                    }
                }
            }

            // 构建命令: cmd /c npx playwright test --reporter=html,json ...
            List<String> command = buildCommand(browser, headed, caseIds, basePath);
            log.info("执行E2E测试命令: {}", String.join(" ", command));

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(new File(basePath));
            pb.redirectErrorStream(true);

            Map<String, String> env = pb.environment();
            env.put("NODE_ENV", "test");
            // 指定JSON报告输出文件
            env.put("PLAYWRIGHT_JSON_OUTPUT_NAME", resultJsonPath.toString());
            // 指定HTML报告输出目录
            env.put("PLAYWRIGHT_HTML_OUTPUT", reportDir.toString());

            Process process = pb.start();
            RUNNING_PROCESSES.put(taskId, process);
            long pid = process.pid();

            E2eTestTask pidUpdate = new E2eTestTask();
            pidUpdate.setTaskId(taskId);
            pidUpdate.setProcessId(pid);
            testTaskMapper.updateById(pidUpdate);

            pushLog(taskId, logLines, "进程启动, PID: " + pid);

            // 读取输出（实时缓冲到日志 + SSE推送）
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    pushLog(taskId, logLines, line);
                    log.debug("[E2E Task {}] {}", taskId, line);
                }
            }

            int exitCode = process.waitFor();

            // ★ 核心修正：exitCode 0或1都表示进程正常跑完
            //   exitCode=1 仅表示"有测试用例失败"，不代表进程异常
            //   只有非0非1才认为是进程崩溃
            E2eTestTask finalUpdate = new E2eTestTask();
            finalUpdate.setTaskId(taskId);
            finalUpdate.setEndTime(LocalDateTime.now());

            if (exitCode == 0 || exitCode == 1) {
                finalUpdate.setStatus("2"); // 已完成（进程正常结束）
                pushLog(taskId, logLines, "测试执行完成，退出码: " + exitCode);
            } else {
                finalUpdate.setStatus("3"); // 进程异常
                finalUpdate.setErrorMsg("进程异常退出，退出码: " + exitCode);
                pushLog(taskId, logLines, "进程异常退出，退出码: " + exitCode);
            }

            // 解析JSON报告中的每条用例结果
            parseJsonResults(resultJsonPath, finalUpdate);

            // 上传报告和产物到MinIO
            String reportUrl = uploadReportToOss(taskId, reportDir, basePath);
            if (reportUrl != null) {
                finalUpdate.setReportPath(reportUrl);
            } else {
                // MinIO upload failed, store local path as fallback
                Path indexPath = reportDir.resolve("index.html");
                if (Files.exists(indexPath)) {
                    finalUpdate.setReportPath("/e2e/report/html/" + taskId + "/");
                }
            }

            uploadArtifactsToOss(taskId, basePath);

            testTaskMapper.updateById(finalUpdate);

        } catch (Exception e) {
            log.error("E2E测试任务执行异常, taskId={}", taskId, e);
            pushLog(taskId, logLines, "执行异常: " + e.getMessage());

            E2eTestTask errUpdate = new E2eTestTask();
            errUpdate.setTaskId(taskId);
            errUpdate.setStatus("3");
            errUpdate.setEndTime(LocalDateTime.now());
            errUpdate.setErrorMsg(e.getMessage());
            testTaskMapper.updateById(errUpdate);
        } finally {
            // Save logs to database
            List<String> logs = LOG_BUFFER.get(taskId);
            if (logs != null && !logs.isEmpty()) {
                String logContent = String.join("\n", logs);
                E2eTestTask logUpdate = new E2eTestTask();
                logUpdate.setTaskId(taskId);
                logUpdate.setLogContent(logContent);
                testTaskMapper.updateById(logUpdate);
            }
            RUNNING_PROCESSES.remove(taskId);
            // 任务结束，关闭SSE连接
            e2eSseManager.complete(taskId);
        }
    }

    /**
     * 构建Playwright命令行
     */
    private List<String> buildCommand(String browser, String headed, List<Long> caseIds, String basePath) {
        List<String> command = new ArrayList<>();
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        if (isWindows) {
            command.add("cmd");
            command.add("/c");
        }
        command.add("npx");
        command.add("playwright");
        command.add("test");

        if (browser != null && !browser.isEmpty()) {
            command.add("--project=" + browser);
        }
        if ("1".equals(headed)) {
            command.add("--headed");
        }

        // 指定spec文件
        if (caseIds != null && !caseIds.isEmpty()) {
            List<String> specFiles = getSpecFilesByCaseIds(caseIds);
            for (String specFile : specFiles) {
                command.add(specFile);
            }
        }

        // ★ 同时输出HTML和JSON报告
        command.add("--reporter=html,json");

        return command;
    }

    /**
     * 解析Playwright JSON报告，统计每条用例结果
     */
    private void parseJsonResults(Path resultJsonPath, E2eTestTask updateTask) {
        if (!Files.exists(resultJsonPath)) {
            log.warn("JSON结果文件不存在: {}", resultJsonPath);
            return;
        }
        try {
            JsonNode root = new ObjectMapper().readTree(resultJsonPath.toFile());
            int[] counts = countFromNode(root);
            updateTask.setPassedCases(counts[0]);
            updateTask.setFailedCases(counts[1]);
            updateTask.setSkippedCases(counts[2]);
            updateTask.setTotalCases(counts[0] + counts[1] + counts[2]);
        } catch (Exception e) {
            log.error("解析JSON结果文件失败: {}", resultJsonPath, e);
        }
    }

    /**
     * 递归统计suites中的用例结果
     */
    private int[] countFromNode(JsonNode node) {
        int passed = 0, failed = 0, skipped = 0;
        JsonNode suites = node.get("suites");
        if (suites != null && suites.isArray()) {
            for (JsonNode suite : suites) {
                int[] sub = countFromSuite(suite);
                passed += sub[0];
                failed += sub[1];
                skipped += sub[2];
            }
        }
        return new int[]{passed, failed, skipped};
    }

    private int[] countFromSuite(JsonNode suite) {
        int passed = 0, failed = 0, skipped = 0;
        JsonNode specs = suite.get("specs");
        if (specs != null && specs.isArray()) {
            for (JsonNode spec : specs) {
                JsonNode tests = spec.get("tests");
                if (tests != null && tests.isArray()) {
                    for (JsonNode test : tests) {
                        String status = test.has("status") ? test.get("status").asText() : "";
                        // Playwright JSON报告: expected=通过, skipped=跳过, 其他(unexpected/flipped/interrupted)=失败
                        if ("expected".equals(status)) {
                            passed++;
                        } else if ("skipped".equals(status)) {
                            skipped++;
                        } else {
                            failed++;
                        }
                    }
                }
            }
        }
        // 递归处理嵌套suites
        JsonNode childSuites = suite.get("suites");
        if (childSuites != null && childSuites.isArray()) {
            for (JsonNode child : childSuites) {
                int[] sub = countFromSuite(child);
                passed += sub[0];
                failed += sub[1];
                skipped += sub[2];
            }
        }
        return new int[]{passed, failed, skipped};
    }

    /**
     * 上传HTML报告目录到MinIO（逐文件上传，保留目录结构）
     * 返回index.html的HTTP URL，可直接在浏览器打开
     */
    private String uploadReportToOss(Long taskId, Path reportDir, String basePath) {
        if (!Files.exists(reportDir)) {
            log.warn("报告目录不存在: {}", reportDir);
            return null;
        }
        OssClient client;
        try {
            client = OssFactory.instance();
        } catch (Exception e) {
            log.error("MinIO上传报告失败，请检查sys_oss_config表是否配置了MinIO: {}", e.getMessage(), e);
            return null;
        }
        String indexUrl = null;
        String prefix = "e2e-report/" + taskId;

        try {
            List<String[]> uploaded = new ArrayList<>();
            Files.walkFileTree(reportDir, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String relativePath = reportDir.relativize(file).toString().replace('\\', '/');
                    String key = prefix + "/" + relativePath;
                    String contentType = guessContentType(relativePath);
                    try {
                        Options opts = Options.builder().setContentType(contentType);
                        PutObjectResult result = client.upload(key, file, opts);
                        uploaded.add(new String[]{relativePath, result.url()});
                        log.debug("报告文件已上传: {} -> {}", relativePath, result.url());
                    } catch (Exception e) {
                        log.error("上传报告文件失败: {}", relativePath, e);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });

            // 找到index.html的URL作为报告入口
            for (String[] entry : uploaded) {
                if ("index.html".equals(entry[0])) {
                    indexUrl = entry[1];
                    break;
                }
            }
            log.info("报告已上传MinIO, 共{}个文件, 入口: {}", uploaded.size(), indexUrl);
        } catch (IOException e) {
            log.error("上传报告目录到MinIO失败", e);
        }
        return indexUrl;
    }

    /**
     * 根据文件扩展名猜测MIME类型
     */
    private String guessContentType(String fileName) {
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".html")) return "text/html";
        if (lower.endsWith(".css")) return "text/css";
        if (lower.endsWith(".js")) return "application/javascript";
        if (lower.endsWith(".json")) return "application/json";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".svg")) return "image/svg+xml";
        if (lower.endsWith(".woff2")) return "font/woff2";
        if (lower.endsWith(".woff")) return "font/woff";
        if (lower.endsWith(".zip")) return "application/zip";
        return "application/octet-stream";
    }

    /**
     * 上传截图、视频、Trace等产物到MinIO
     */
    private void uploadArtifactsToOss(Long taskId, String basePath) {
        Path testResultsDir = Paths.get(basePath, "test-results");
        if (!Files.exists(testResultsDir)) {
            log.warn("产物目录不存在: {}", testResultsDir);
            return;
        }

        OssClient client;
        try {
            client = OssFactory.instance();
        } catch (Exception e) {
            log.error("MinIO上传产物失败，请检查sys_oss_config表是否配置了MinIO: {}", e.getMessage(), e);
            return;
        }

        try {
            Files.walkFileTree(testResultsDir, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    String fileName = file.getFileName().toString().toLowerCase();
                    String artifactType;
                    String contentType;

                    if (fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                        artifactType = "screenshot";
                        contentType = fileName.endsWith(".png") ? "image/png" : "image/jpeg";
                    } else if (fileName.endsWith(".webm") || fileName.endsWith(".mp4")) {
                        artifactType = "video";
                        contentType = fileName.endsWith(".mp4") ? "video/mp4" : "video/webm";
                    } else if (fileName.endsWith(".zip") && fileName.contains("trace")) {
                        artifactType = "trace";
                        contentType = "application/zip";
                    } else {
                        return FileVisitResult.CONTINUE;
                    }

                    try {
                        String key = client.buildPathKey("e2e-artifact", file.getFileName().toString());
                        Options opts = Options.builder().setContentType(contentType);
                        PutObjectResult result = client.upload(key, file, opts);

                        E2eTestArtifact artifact = new E2eTestArtifact();
                        artifact.setTaskId(taskId);
                        artifact.setArtifactType(artifactType);
                        artifact.setFilePath(result.url());
                        artifact.setFileSize(attrs.size());
                        artifact.setCreateTime(LocalDateTime.now());

                        // 从路径提取用例名 (test-results/xxx/test-name/...)
                        Path relativePath = testResultsDir.relativize(file);
                        if (relativePath.getNameCount() > 1) {
                            artifact.setCaseName(relativePath.getName(0).toString());
                        }
                        testArtifactMapper.insert(artifact);

                        log.debug("产物已上传: {} -> {}", file.getFileName(), result.url());
                    } catch (Exception e) {
                        log.error("MinIO上传产物失败, file={}: {}", file.toAbsolutePath(), e.getMessage(), e);
                        // Store local path as fallback
                        E2eTestArtifact artifact = new E2eTestArtifact();
                        artifact.setTaskId(taskId);
                        artifact.setArtifactType(artifactType);
                        artifact.setFilePath("LOCAL:" + file.toAbsolutePath());
                        artifact.setFileSize(attrs.size());
                        artifact.setCreateTime(LocalDateTime.now());

                        Path relativePath = testResultsDir.relativize(file);
                        if (relativePath.getNameCount() > 1) {
                            artifact.setCaseName(relativePath.getName(0).toString());
                        }
                        testArtifactMapper.insert(artifact);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            log.error("遍历产物目录失败", e);
        }
    }

    /**
     * 根据用例ID列表获取spec文件路径
     */
    private List<String> getSpecFilesByCaseIds(List<Long> caseIds) {
        return testCaseMapper.selectBatchIds(caseIds).stream()
            .map(E2eTestCase::getSpecFile)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());
    }

    @Override
    public void stopTask(Long taskId) {
        E2eTestTask task = testTaskMapper.selectById(taskId);
        if (task == null) return;

        Process process = RUNNING_PROCESSES.get(taskId);
        if (process != null && process.isAlive()) {
            process.destroyForcibly();
            RUNNING_PROCESSES.remove(taskId);
        }

        Long pid = task.getProcessId();
        if (pid != null) {
            killProcessByPid(pid);
        }

        List<String> logLines = LOG_BUFFER.get(taskId);
        if (logLines != null) {
            pushLog(taskId, logLines, "任务已被手动停止");
        }

        E2eTestTask updateTask = new E2eTestTask();
        updateTask.setTaskId(taskId);
        updateTask.setStatus("4");
        updateTask.setEndTime(LocalDateTime.now());
        // Save logs to database
        if (logLines != null && !logLines.isEmpty()) {
            updateTask.setLogContent(String.join("\n", logLines));
        }
        testTaskMapper.updateById(updateTask);

        e2eSseManager.complete(taskId);
    }

    private void killProcessByPid(Long pid) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder pb;
            if (os.contains("win")) {
                pb = new ProcessBuilder("taskkill", "/F", "/T", "/PID", String.valueOf(pid));
            } else {
                pb = new ProcessBuilder("kill", "-9", "-" + pid);
            }
            pb.start().waitFor();
        } catch (Exception e) {
            log.error("杀掉进程失败, PID={}", pid, e);
        }
    }

    @Override
    public int deleteTaskByIds(Long[] taskIds) {
        for (Long taskId : taskIds) {
            LambdaQueryWrapper<E2eTestTaskCase> wrapper = Wrappers.lambdaQuery();
            wrapper.eq(E2eTestTaskCase::getTaskId, taskId);
            testTaskCaseMapper.delete(wrapper);
        }
        return testTaskMapper.deleteByIds(Arrays.asList(taskIds));
    }

    @Override
    public List<String> getTaskLogs(Long taskId) {
        List<String> logLines = LOG_BUFFER.get(taskId);
        if (logLines != null && !logLines.isEmpty()) {
            return new ArrayList<>(logLines);
        }
        // Read from database for completed tasks
        E2eTestTask task = testTaskMapper.selectById(taskId);
        if (task != null && task.getLogContent() != null && !task.getLogContent().isEmpty()) {
            return Arrays.asList(task.getLogContent().split("\n"));
        }
        return Collections.emptyList();
    }

    private Wrapper<E2eTestTask> buildQueryWrapper(E2eTestTaskBo bo) {
        Map<String, Object> params = bo.getParams();
        return testTaskMapper.lambda()
            .likeIfText(E2eTestTask::getTaskName, bo.getTaskName())
            .eqIfText(E2eTestTask::getBrowser, bo.getBrowser())
            .eqIfText(E2eTestTask::getStatus, bo.getStatus())
            .betweenParams(E2eTestTask::getCreateTime, params, "beginTime", "endTime")
            .orderByDesc(E2eTestTask::getCreateTime);
    }
}
