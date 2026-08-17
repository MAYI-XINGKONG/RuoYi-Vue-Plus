package org.dromara.test.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.PageResult;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.web.core.BaseController;
import org.dromara.test.domain.vo.TestReportVo;
import org.dromara.test.service.ITestReportService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * 测试报告Controller
 *
 * @author autotest
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/test/report")
public class TestReportController extends BaseController {

    private final ITestReportService testReportService;

    /**
     * 查询测试报告列表
     */
    @SaCheckPermission("test:report:list")
    @GetMapping("/list")
    public R<PageResult<TestReportVo>> list(PageQuery pageQuery) {
        return R.ok(testReportService.queryPageList(pageQuery));
    }

    /**
     * 获取测试报告详细信息
     *
     * @param id 报告ID
     */
    @SaCheckPermission("test:report:query")
    @GetMapping("/{id}")
    public R<TestReportVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable("id") Long id) {
        return R.ok(testReportService.queryById(id));
    }

    /**
     * 删除测试报告
     *
     * @param ids 报告ID串
     */
    @SaCheckPermission("test:report:remove")
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(testReportService.deleteWithValidByIds(Arrays.asList(ids)));
    }
}
