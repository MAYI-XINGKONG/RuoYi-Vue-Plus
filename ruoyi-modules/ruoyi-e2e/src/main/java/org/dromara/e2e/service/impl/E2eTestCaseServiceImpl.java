package org.dromara.e2e.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.PageResult;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.e2e.config.E2eProperties;
import org.dromara.e2e.domain.E2eTestCase;
import org.dromara.e2e.domain.E2eTestCaseHistory;
import org.dromara.e2e.domain.bo.E2eTestCaseBo;
import org.dromara.e2e.domain.vo.E2eTestCaseHistoryVo;
import org.dromara.e2e.domain.vo.E2eTestCaseVo;
import org.dromara.e2e.mapper.E2eTestCaseHistoryMapper;
import org.dromara.e2e.mapper.E2eTestCaseMapper;
import org.dromara.e2e.service.IE2eTestCaseService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * E2E测试用例 服务层处理
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class E2eTestCaseServiceImpl implements IE2eTestCaseService {

    private final E2eTestCaseMapper testCaseMapper;
    private final E2eTestCaseHistoryMapper testCaseHistoryMapper;
    private final E2eProperties e2eProperties;

    /**
     * 分页查询测试用例列表
     */
    @Override
    public PageResult<E2eTestCaseVo> selectPageTestCaseList(E2eTestCaseBo bo, PageQuery pageQuery) {
        Page<E2eTestCaseVo> page = testCaseMapper.selectVoPage(pageQuery.build(), buildQueryWrapper(bo));
        return PageResult.build(page.getRecords(), page.getTotal());
    }

    /**
     * 通过用例ID查询测试用例信息
     */
    @Override
    public E2eTestCaseVo selectTestCaseById(Long caseId) {
        return testCaseMapper.selectVoById(caseId);
    }

    /**
     * 新增测试用例
     */
    @Override
    public int insertTestCase(E2eTestCaseBo bo) {
        // Auto-generate specFile if not provided
        if (bo.getSpecFile() == null || bo.getSpecFile().isEmpty()) {
            String safeName = bo.getCaseName().replaceAll("[^a-zA-Z0-9\\u4e00-\\u9fa5]+", "-").replaceAll("^-+|-+$", "");
            String group = (bo.getCaseGroup() != null && !bo.getCaseGroup().isEmpty()) ? bo.getCaseGroup() : "default";
            bo.setSpecFile("tests/" + group + "/" + safeName + ".spec.ts");
        }
        // Auto-generate content template if not provided
        if (bo.getContent() == null || bo.getContent().isEmpty()) {
            String group = bo.getCaseGroup() != null ? bo.getCaseGroup() : "default";
            bo.setContent("import { test, expect } from '@playwright/test';\n\n"
                + "test.describe('" + group + "', () => {\n"
                + "  test('" + bo.getCaseName() + "', async ({ page }) => {\n"
                + "    // TODO: 编写测试步骤\n"
                + "    await expect(page).toHaveTitle(/.*/);\n"
                + "  });\n"
                + "});\n");
        }
        E2eTestCase testCase = MapstructUtils.convert(bo, E2eTestCase.class);
        int rows = testCaseMapper.insert(testCase);
        if (rows > 0 && testCase.getContent() != null) {
            saveHistory(testCase.getCaseId(), testCase.getContent(), 1);
        }
        return rows;
    }

    /**
     * 修改测试用例
     */
    @Override
    public int updateTestCase(E2eTestCaseBo bo) {
        // 名称或分组变化时，同步更新specFile
        if (bo.getCaseName() != null || bo.getCaseGroup() != null) {
            E2eTestCaseVo old = testCaseMapper.selectVoById(bo.getCaseId());
            if (old != null) {
                String name = bo.getCaseName() != null ? bo.getCaseName() : old.getCaseName();
                String group = bo.getCaseGroup() != null ? bo.getCaseGroup() : old.getCaseGroup();
                if (group == null || group.isEmpty()) group = "default";
                String safeName = name.replaceAll("[^a-zA-Z0-9\\u4e00-\\u9fa5]+", "-").replaceAll("^-+|-+$", "");
                bo.setSpecFile("tests/" + group + "/" + safeName + ".spec.ts");
            }
        }
        E2eTestCase testCase = MapstructUtils.convert(bo, E2eTestCase.class);
        int rows = testCaseMapper.updateById(testCase);
        if (rows > 0 && bo.getContent() != null) {
            int maxVer = 0;
            E2eTestCaseHistory latest = testCaseHistoryMapper.lambda()
                .eq(E2eTestCaseHistory::getCaseId, bo.getCaseId())
                .orderByDesc(E2eTestCaseHistory::getVersion)
                .last("LIMIT 1")
                .one();
            if (latest != null && latest.getVersion() != null) {
                maxVer = latest.getVersion();
            }
            saveHistory(bo.getCaseId(), bo.getContent(), maxVer + 1);
        }
        return rows;
    }

    /**
     * 保存历史版本
     */
    private void saveHistory(Long caseId, String content, int version) {
        E2eTestCaseHistory history = new E2eTestCaseHistory();
        history.setCaseId(caseId);
        history.setContent(content);
        history.setVersion(version);
        history.setCreateTime(LocalDateTime.now());
        testCaseHistoryMapper.insert(history);
    }

    /**
     * 查询用例历史版本
     */
    @Override
    public List<E2eTestCaseHistoryVo> selectCaseHistory(Long caseId) {
        return testCaseHistoryMapper.lambda()
            .eq(E2eTestCaseHistory::getCaseId, caseId)
            .orderByDesc(E2eTestCaseHistory::getVersion)
            .voList();
    }

    /**
     * 根据历史ID查询历史版本
     */
    @Override
    public E2eTestCaseHistoryVo selectHistoryById(Long historyId) {
        return testCaseHistoryMapper.selectVoById(historyId);
    }

    /**
     * 回退到指定版本
     */
    @Override
    public void revertToVersion(Long caseId, Long historyId) {
        E2eTestCaseHistoryVo history = testCaseHistoryMapper.selectVoById(historyId);
        if (history != null) {
            E2eTestCase update = new E2eTestCase();
            update.setCaseId(caseId);
            update.setContent(history.getContent());
            testCaseMapper.updateById(update);
            // Save as new version
            int maxVer = 0;
            E2eTestCaseHistory latest = testCaseHistoryMapper.lambda()
                .eq(E2eTestCaseHistory::getCaseId, caseId)
                .orderByDesc(E2eTestCaseHistory::getVersion)
                .last("LIMIT 1")
                .one();
            if (latest != null && latest.getVersion() != null) {
                maxVer = latest.getVersion();
            }
            saveHistory(caseId, history.getContent(), maxVer + 1);
        }
    }

    /**
     * 仅更新用例内容（不影响名称等元数据）
     */
    @Override
    public void updateCaseContent(Long caseId, String content) {
        E2eTestCase update = new E2eTestCase();
        update.setCaseId(caseId);
        update.setContent(content);
        testCaseMapper.updateById(update);

        // 保存历史版本
        int maxVer = 0;
        E2eTestCaseHistory latest = testCaseHistoryMapper.lambda()
            .eq(E2eTestCaseHistory::getCaseId, caseId)
            .orderByDesc(E2eTestCaseHistory::getVersion)
            .last("LIMIT 1")
            .one();
        if (latest != null && latest.getVersion() != null) {
            maxVer = latest.getVersion();
        }
        saveHistory(caseId, content, maxVer + 1);
    }

    /**
     * 批量删除测试用例
     */
    @Override
    public int deleteTestCaseByIds(Long[] caseIds) {
        return testCaseMapper.deleteByIds(Arrays.asList(caseIds));
    }

    /**
     * 同步spec文件到数据库
     */
    @Override
    public void syncSpecFiles() {
        String basePath = e2eProperties.getResolvedBasePath();
        File testsDir = new File(basePath, "tests");
        if (!testsDir.exists() || !testsDir.isDirectory()) {
            log.warn("E2E测试目录不存在: {}", testsDir.getAbsolutePath());
            return;
        }
        scanAndSyncFiles(testsDir, testsDir.getAbsolutePath());
    }

    /**
     * 递归扫描目录并同步spec文件
     */
    private void scanAndSyncFiles(File dir, String basePath) {
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                scanAndSyncFiles(file, basePath);
            } else if (file.getName().endsWith(".spec.ts") || file.getName().endsWith(".spec.js")) {
                syncSingleSpecFile(file, basePath);
            }
        }
    }

    /**
     * 同步单个spec文件
     */
    private void syncSingleSpecFile(File file, String basePath) {
        try {
            String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            String relativePath = Paths.get(basePath).relativize(file.toPath()).toString().replace('\\', '/');

            // 提取测试名称: test('name') 或 test("name")
            Pattern pattern = Pattern.compile("test\\s*\\(['\"](.+?)['\"]");
            Matcher matcher = pattern.matcher(content);
            List<String> testNames = new ArrayList<>();
            while (matcher.find()) {
                testNames.add(matcher.group(1));
            }

            // 提取分组: describe('name')
            Pattern describePattern = Pattern.compile("describe\\s*\\(['\"](.+?)['\"]");
            Matcher describeMatcher = describePattern.matcher(content);
            String caseGroup = "";
            if (describeMatcher.find()) {
                caseGroup = describeMatcher.group(1);
            }

            // 为每个test用例创建/更新记录
            for (String testName : testNames) {
                String caseName = testName;
                boolean exist = testCaseMapper.lambda()
                    .eq(E2eTestCase::getSpecFile, relativePath)
                    .eq(E2eTestCase::getCaseName, caseName)
                    .exists();
                if (!exist) {
                    E2eTestCase testCase = new E2eTestCase();
                    testCase.setCaseName(caseName);
                    testCase.setSpecFile(relativePath);
                    testCase.setCaseGroup(caseGroup);
                    testCase.setContent(content);
                    testCase.setStatus("0");
                    testCaseMapper.insert(testCase);
                }
            }

            // 如果没有找到test用例但文件存在，记录文件级别的信息
            if (testNames.isEmpty()) {
                boolean exist = testCaseMapper.lambda()
                    .eq(E2eTestCase::getSpecFile, relativePath)
                    .exists();
                if (!exist) {
                    E2eTestCase testCase = new E2eTestCase();
                    testCase.setCaseName(file.getName());
                    testCase.setSpecFile(relativePath);
                    testCase.setCaseGroup(caseGroup);
                    testCase.setContent(content);
                    testCase.setStatus("0");
                    testCaseMapper.insert(testCase);
                }
            }
        } catch (IOException e) {
            log.error("读取spec文件失败: {}", file.getAbsolutePath(), e);
        }
    }

    /**
     * 根据分组查询测试用例列表
     */
    @Override
    public List<E2eTestCaseVo> selectTestCaseByGroup(String caseGroup) {
        LambdaQueryWrapper<E2eTestCase> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(E2eTestCase::getCaseGroup, caseGroup);
        return testCaseMapper.selectVoList(wrapper);
    }

    /**
     * 根据查询条件构建查询包装器
     */
    private Wrapper<E2eTestCase> buildQueryWrapper(E2eTestCaseBo bo) {
        Map<String, Object> params = bo.getParams();
        return testCaseMapper.lambda()
            .likeIfText(E2eTestCase::getCaseName, bo.getCaseName())
            .likeIfText(E2eTestCase::getSpecFile, bo.getSpecFile())
            .eqIfText(E2eTestCase::getCaseGroup, bo.getCaseGroup())
            .eqIfText(E2eTestCase::getStatus, bo.getStatus())
            .betweenParams(E2eTestCase::getCreateTime, params, "beginTime", "endTime")
            .orderByDesc(E2eTestCase::getCreateTime);
    }
}
