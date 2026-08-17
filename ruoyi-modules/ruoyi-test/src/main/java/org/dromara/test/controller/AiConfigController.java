package org.dromara.test.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.web.core.BaseController;
import org.dromara.test.domain.bo.AiConfigBo;
import org.dromara.test.domain.vo.AiConfigVo;
import org.dromara.test.service.IAiConfigService;
import org.dromara.test.service.IAiService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * AI配置Controller
 *
 * @author autotest
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/test/aiConfig")
public class AiConfigController extends BaseController {

    private final IAiConfigService aiConfigService;
    private final IAiService aiService;

    /**
     * 查询AI配置列表
     */
    @SaCheckPermission("test:aiConfig:list")
    @GetMapping("/list")
    public R<List<AiConfigVo>> list() {
        return R.ok(aiConfigService.queryList());
    }

    /**
     * 获取AI配置详细信息
     *
     * @param id 配置ID
     */
    @SaCheckPermission("test:aiConfig:query")
    @GetMapping("/{id}")
    public R<AiConfigVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable("id") Long id) {
        return R.ok(aiConfigService.queryById(id));
    }

    /**
     * 新增AI配置
     */
    @SaCheckPermission("test:aiConfig:add")
    @Log(title = "AI配置", businessType = BusinessType.INSERT)
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody AiConfigBo bo) {
        return toAjax(aiConfigService.insertByBo(bo) != null);
    }

    /**
     * 修改AI配置
     */
    @SaCheckPermission("test:aiConfig:edit")
    @Log(title = "AI配置", businessType = BusinessType.UPDATE)
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody AiConfigBo bo) {
        return toAjax(aiConfigService.updateByBo(bo));
    }

    /**
     * 删除AI配置
     *
     * @param ids 配置ID串
     */
    @SaCheckPermission("test:aiConfig:remove")
    @Log(title = "AI配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(aiConfigService.deleteWithValidByIds(Arrays.asList(ids)));
    }

    /**
     * 获取可用模型列表（通过配置ID）
     *
     * @param id 配置ID
     */
    @SaCheckPermission("test:aiConfig:fetchModels")
    @PostMapping("/fetchModels/{id}")
    public R<List<String>> fetchModels(@NotNull(message = "主键不能为空") @PathVariable("id") Long id) {
        return R.ok(aiService.fetchModels(id));
    }

    /**
     * 获取可用模型列表（通过API地址和密钥，用于新增配置时）
     */
    @SaCheckPermission("test:aiConfig:fetchModels")
    @PostMapping("/fetchModels")
    public R<List<String>> fetchModelsByCredentials(@RequestBody AiConfigBo bo) {
        return R.ok(aiService.fetchModelsByUrlAndKey(bo.getApiUrl(), bo.getApiKey()));
    }
}
