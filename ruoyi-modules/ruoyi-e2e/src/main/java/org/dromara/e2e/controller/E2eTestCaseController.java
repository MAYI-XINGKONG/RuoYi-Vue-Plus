package org.dromara.e2e.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.PageResult;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.redis.annotation.RepeatSubmit;
import org.dromara.common.web.core.BaseController;
import org.dromara.e2e.domain.bo.E2eTestCaseBo;
import org.dromara.e2e.domain.vo.E2eTestCaseHistoryVo;
import org.dromara.e2e.domain.vo.E2eTestCaseVo;
import org.dromara.e2e.service.IE2eTestCaseService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * E2E测试用例操作处理
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/e2e/case")
public class E2eTestCaseController extends BaseController {

    private final IE2eTestCaseService testCaseService;

    /**
     * 分页查询测试用例列表
     */
    @SaCheckPermission("e2e:testcase:list")
    @GetMapping("/list")
    public R<PageResult<E2eTestCaseVo>> list(E2eTestCaseBo bo, PageQuery pageQuery) {
        return R.ok(testCaseService.selectPageTestCaseList(bo, pageQuery));
    }

    /**
     * 根据用例编号获取详细信息
     */
    @SaCheckPermission("e2e:testcase:query")
    @GetMapping(value = "/{caseId}")
    public R<E2eTestCaseVo> getInfo(@NotNull @PathVariable Long caseId) {
        return R.ok(testCaseService.selectTestCaseById(caseId));
    }

    /**
     * 新增测试用例
     */
    @SaCheckPermission("e2e:testcase:add")
    @Log(title = "E2E测试用例", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping
    public R<Void> add(@Validated @RequestBody E2eTestCaseBo bo) {
        return toAjax(testCaseService.insertTestCase(bo));
    }

    /**
     * 修改测试用例
     */
    @SaCheckPermission("e2e:testcase:edit")
    @Log(title = "E2E测试用例", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping
    public R<Void> edit(@Validated @RequestBody E2eTestCaseBo bo) {
        return toAjax(testCaseService.updateTestCase(bo));
    }

    /**
     * 删除测试用例
     */
    @SaCheckPermission("e2e:testcase:remove")
    @Log(title = "E2E测试用例", businessType = BusinessType.DELETE)
    @DeleteMapping("/{caseIds}")
    public R<Void> remove(@PathVariable Long[] caseIds) {
        return toAjax(testCaseService.deleteTestCaseByIds(caseIds));
    }

    /**
     * 仅保存用例代码内容（不影响名称等元数据）
     */
    @SaCheckPermission("e2e:testcase:edit")
    @Log(title = "E2E测试用例-保存代码", businessType = BusinessType.UPDATE)
    @PutMapping("/content")
    public R<Void> saveContent(@RequestParam Long caseId, @RequestBody String content) {
        testCaseService.updateCaseContent(caseId, content);
        return R.ok();
    }

    /**
     * 查询用例历史版本
     */
    @SaCheckPermission("e2e:testcase:query")
    @GetMapping("/history/{caseId}")
    public R<List<E2eTestCaseHistoryVo>> getHistory(@PathVariable Long caseId) {
        return R.ok(testCaseService.selectCaseHistory(caseId));
    }

    /**
     * 回退到指定版本
     */
    @SaCheckPermission("e2e:testcase:edit")
    @Log(title = "E2E测试用例-版本回退", businessType = BusinessType.UPDATE)
    @PostMapping("/revert")
    public R<Void> revert(@RequestParam Long caseId, @RequestParam Long historyId) {
        testCaseService.revertToVersion(caseId, historyId);
        return R.ok();
    }

    /**
     * 同步spec文件
     */
    @SaCheckPermission("e2e:testcase:sync")
    @Log(title = "E2E测试用例-同步文件", businessType = BusinessType.IMPORT)
    @PostMapping("/sync")
    public R<Void> sync() {
        testCaseService.syncSpecFiles();
        return R.ok();
    }
}
