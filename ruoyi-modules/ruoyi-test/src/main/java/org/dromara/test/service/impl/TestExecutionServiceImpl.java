package org.dromara.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.PageResult;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.test.domain.TestExecution;
import org.dromara.test.domain.bo.TestExecutionBo;
import org.dromara.test.domain.vo.TestExecutionVo;
import org.dromara.test.mapper.TestExecutionMapper;
import org.dromara.test.service.ITestExecutionService;
import cn.hutool.core.util.IdUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * 测试执行Service业务层处理
 *
 * @author autotest
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class TestExecutionServiceImpl implements ITestExecutionService {

    private final TestExecutionMapper testExecutionMapper;

    @Value("${autotest.e2e-path:../e2e-test}")
    private String e2ePath;

    /**
     * 根据主键查询测试执行详情
     *
     * @param id 主键
     * @return 测试执行视图对象
     */
    @Override
    public TestExecutionVo queryById(Long id) {
        return testExecutionMapper.selectVoById(id);
    }

    /**
     * 分页查询测试执行列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    @Override
    public PageResult<TestExecutionVo> queryPageList(TestExecutionBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<TestExecution> lqw = buildQueryWrapper(bo);
        Page<TestExecutionVo> result = testExecutionMapper.selectVoPage(pageQuery.build(), lqw);
        return PageResult.build(result.getRecords(), result.getTotal());
    }

    /**
     * 构建测试执行动态查询条件
     *
     * @param bo 查询条件
     * @return 查询条件包装器
     */
    private LambdaQueryWrapper<TestExecution> buildQueryWrapper(TestExecutionBo bo) {
        LambdaQueryWrapper<TestExecution> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getSuiteId() != null, TestExecution::getSuiteId, bo.getSuiteId());
        lqw.eq(StringUtils.isNotBlank(bo.getExecType()), TestExecution::getExecType, bo.getExecType());
        lqw.eq(StringUtils.isNotBlank(bo.getBrowser()), TestExecution::getBrowser, bo.getBrowser());
        lqw.orderByDesc(TestExecution::getCreateTime);
        return lqw;
    }

    /**
     * 运行测试
     *
     * @param bo 测试执行业务对象
     * @return 执行记录ID
     */
    @Override
    public Long runTest(TestExecutionBo bo) {
        Long executionId = IdUtil.getSnowflakeNextId();
        String browser = StringUtils.isNotBlank(bo.getBrowser()) ? bo.getBrowser() : "chromium";

        // 创建执行记录
        TestExecution execution = new TestExecution();
        execution.setExecutionId(executionId);
        execution.setSuiteId(bo.getSuiteId());
        execution.setExecType(bo.getExecType());
        execution.setBrowser(browser);
        execution.setStatus("running");
        execution.setStartTime(LocalDateTime.now());
        testExecutionMapper.insert(execution);

        // 异步执行测试
        CompletableFuture.runAsync(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder(
                    "cmd", "/c", "npx", "playwright", "test", "--project=" + browser
                );
                pb.directory(new File(e2ePath));
                pb.redirectErrorStream(true);
                Process process = pb.start();

                StringBuilder logBuilder = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        logBuilder.append(line).append("\n");
                    }
                }

                int exitCode = process.waitFor();
                LocalDateTime endTime = LocalDateTime.now();

                // 更新执行记录
                TestExecution update = new TestExecution();
                update.setExecutionId(executionId);
                update.setStatus(exitCode == 0 ? "pass" : "fail");
                update.setLogOutput(logBuilder.toString());
                update.setEndTime(endTime);
                update.setDuration(java.time.Duration.between(execution.getStartTime(), endTime).toMillis());
                testExecutionMapper.updateById(update);

            } catch (Exception e) {
                log.error("测试执行失败", e);
                TestExecution update = new TestExecution();
                update.setExecutionId(executionId);
                update.setStatus("fail");
                update.setLogOutput("执行异常: " + e.getMessage());
                update.setEndTime(LocalDateTime.now());
                testExecutionMapper.updateById(update);
            }
        });

        return executionId;
    }
}
