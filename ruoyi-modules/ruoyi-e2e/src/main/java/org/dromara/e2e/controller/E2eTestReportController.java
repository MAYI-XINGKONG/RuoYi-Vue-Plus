package org.dromara.e2e.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.PageResult;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.web.core.BaseController;
import org.dromara.e2e.config.E2eProperties;
import org.dromara.e2e.domain.bo.E2eTestTaskBo;
import org.dromara.e2e.domain.vo.E2eTestArtifactVo;
import org.dromara.e2e.domain.vo.E2eTestTaskVo;
import org.dromara.e2e.service.IE2eTestReportService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * E2E测试报告操作处理
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/e2e/report")
public class E2eTestReportController extends BaseController {

    private final IE2eTestReportService testReportService;
    private final E2eProperties e2eProperties;

    /**
     * 分页查询测试报告列表
     */
    @SaCheckPermission("e2e:report:list")
    @GetMapping("/list")
    public R<PageResult<E2eTestTaskVo>> list(E2eTestTaskBo bo, PageQuery pageQuery) {
        return R.ok(testReportService.selectPageReportList(bo, pageQuery));
    }

    /**
     * 根据任务编号获取报告详情
     */
    @SaCheckPermission("e2e:report:query")
    @GetMapping(value = "/{taskId}")
    public R<E2eTestTaskVo> getInfo(@NotNull @PathVariable Long taskId) {
        return R.ok(testReportService.selectReportById(taskId));
    }

    /**
     * 根据任务ID获取测试产物列表
     */
    @SaCheckPermission("e2e:report:query")
    @GetMapping("/artifacts/{taskId}")
    public R<List<E2eTestArtifactVo>> getArtifacts(@NotNull @PathVariable Long taskId) {
        return R.ok(testReportService.selectArtifactsByTaskId(taskId));
    }

    /**
     * 获取报告文件内容
     */
    @SaCheckPermission("e2e:report:query")
    @GetMapping("/file")
    public R<String> getFile(@RequestParam String path) {
        try {
            Path filePath = Paths.get(path);
            if (!Files.exists(filePath)) {
                return R.fail("文件不存在");
            }
            String content = Files.readString(filePath);
            return R.ok(content);
        } catch (IOException e) {
            return R.fail("读取文件失败: " + e.getMessage());
        }
    }

    /**
     * 本地报告文件服务（当MinIO不可用时的降级方案）
     */
    @GetMapping("/html/{taskId}/**")
    public void serveReportFile(@NotNull @PathVariable Long taskId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String basePath = e2eProperties.getResolvedBasePath();
        Path reportDir = Paths.get(basePath, "playwright-report");

        // Extract the relative file path from the URL
        String requestURI = request.getRequestURI();
        String prefix = "/e2e/report/html/" + taskId + "/";
        int startIdx = requestURI.indexOf(prefix);
        String relativePath = startIdx >= 0 ? requestURI.substring(startIdx + prefix.length()) : "index.html";
        if (relativePath.isEmpty()) relativePath = "index.html";

        Path file = reportDir.resolve(relativePath);
        if (!Files.exists(file) || !file.startsWith(reportDir)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String contentType = Files.probeContentType(file);
        response.setContentType(contentType != null ? contentType : "application/octet-stream");
        response.setHeader("Content-Disposition", "inline");
        Files.copy(file, response.getOutputStream());
    }

    /**
     * 本地产物文件服务（当MinIO不可用时的降级方案）
     */
    @GetMapping("/artifact")
    public void serveArtifact(@RequestParam String path, HttpServletResponse response) throws IOException {
        String realPath = path;
        if (path.startsWith("LOCAL:")) {
            realPath = path.substring(6);
        }
        Path file = Paths.get(realPath);
        if (!Files.exists(file)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String contentType = Files.probeContentType(file);
        response.setContentType(contentType != null ? contentType : "application/octet-stream");
        response.setHeader("Content-Disposition", "inline");
        Files.copy(file, response.getOutputStream());
    }

    /**
     * 删除测试报告
     */
    @SaCheckPermission("e2e:report:remove")
    @Log(title = "E2E测试报告", businessType = BusinessType.DELETE)
    @DeleteMapping("/{taskIds}")
    public R<Void> remove(@PathVariable Long[] taskIds) {
        return toAjax(testReportService.deleteReportByIds(taskIds));
    }
}
