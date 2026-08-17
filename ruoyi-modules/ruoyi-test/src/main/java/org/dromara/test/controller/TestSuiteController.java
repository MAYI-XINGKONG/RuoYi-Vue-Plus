package org.dromara.test.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.PageResult;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.excel.utils.ExcelBuilder;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.redis.annotation.RepeatSubmit;
import org.dromara.common.web.core.BaseController;
import org.dromara.test.domain.bo.TestSuiteBo;
import org.dromara.test.domain.vo.TestSuiteVo;
import org.dromara.test.service.IAiService;
import org.dromara.test.service.ITestSuiteService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 测试套件Controller
 *
 * @author autotest
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/test/suite")
public class TestSuiteController extends BaseController {

    private final ITestSuiteService testSuiteService;
    private final IAiService aiService;

    /**
     * 查询测试套件列表
     */
    @SaCheckPermission("test:suite:list")
    @GetMapping("/list")
    public R<PageResult<TestSuiteVo>> list(TestSuiteBo bo, PageQuery pageQuery) {
        return R.ok(testSuiteService.queryPageList(bo, pageQuery));
    }

    /**
     * 获取测试套件详细信息
     *
     * @param id 套件ID
     */
    @SaCheckPermission("test:suite:query")
    @GetMapping("/{id}")
    public R<TestSuiteVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable("id") Long id) {
        return R.ok(testSuiteService.queryById(id));
    }

    /**
     * 新增测试套件
     */
    @SaCheckPermission("test:suite:add")
    @Log(title = "测试套件", businessType = BusinessType.INSERT)
    @RepeatSubmit(interval = 2, timeUnit = TimeUnit.SECONDS, message = "{repeat.submit.message}")
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody TestSuiteBo bo) {
        return toAjax(testSuiteService.insertByBo(bo) != null);
    }

    /**
     * 修改测试套件
     */
    @SaCheckPermission("test:suite:edit")
    @Log(title = "测试套件", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody TestSuiteBo bo) {
        return toAjax(testSuiteService.updateByBo(bo));
    }

    /**
     * 删除测试套件
     *
     * @param ids 套件ID串
     */
    @SaCheckPermission("test:suite:remove")
    @Log(title = "测试套件", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(testSuiteService.deleteWithValidByIds(Arrays.asList(ids)));
    }

    /**
     * 导出测试套件列表
     */
    @SaCheckPermission("test:suite:export")
    @Log(title = "测试套件", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(TestSuiteBo bo, HttpServletResponse response) {
        List<TestSuiteVo> list = testSuiteService.queryList(bo);
        ExcelBuilder.of(list, TestSuiteVo.class).sheetName("测试套件").toResponse(response);
    }

    /**
     * AI生成测试用例
     *
     * @param description 测试用例描述
     * @param configId    AI配置ID
     */
    @SaCheckPermission("test:suite:aiGenerate")
    @PostMapping("/aiGenerate")
    public R<String> aiGenerate(@RequestBody String description,
                                @RequestParam(required = false) Long configId) {
        return R.ok(aiService.generateTestCase(description, configId));
    }
}
