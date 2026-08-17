package org.dromara.test.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.PageResult;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.web.core.BaseController;
import org.dromara.test.domain.bo.TestExecutionBo;
import org.dromara.test.domain.vo.TestExecutionVo;
import org.dromara.test.service.ITestExecutionService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 测试执行Controller
 *
 * @author autotest
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/test/execution")
public class TestExecutionController extends BaseController {

    private final ITestExecutionService testExecutionService;

    /**
     * 查询测试执行列表
     */
    @SaCheckPermission("test:execution:list")
    @GetMapping("/list")
    public R<PageResult<TestExecutionVo>> list(TestExecutionBo bo, PageQuery pageQuery) {
        return R.ok(testExecutionService.queryPageList(bo, pageQuery));
    }

    /**
     * 获取测试执行详细信息
     *
     * @param id 执行ID
     */
    @SaCheckPermission("test:execution:query")
    @GetMapping("/{id}")
    public R<TestExecutionVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable("id") Long id) {
        return R.ok(testExecutionService.queryById(id));
    }

    /**
     * 运行测试
     */
    @SaCheckPermission("test:execution:run")
    @PostMapping("/run")
    public R<Long> runTest(@RequestBody TestExecutionBo bo) {
        Long executionId = testExecutionService.runTest(bo);
        return R.ok(executionId);
    }
}
