package org.dromara.test.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.test.domain.AiConfig;

import java.io.Serial;
import java.io.Serializable;

/**
 * AI配置业务对象 ai_config
 *
 * @author autotest
 */
@Data
@AutoMapper(target = AiConfig.class, reverseConvertGenerate = false)
public class AiConfigBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 配置ID
     */
    @NotNull(message = "配置ID不能为空", groups = {EditGroup.class})
    private Long configId;

    /**
     * 配置名称
     */
    @NotBlank(message = "配置名称不能为空", groups = {AddGroup.class, EditGroup.class})
    private String configName;

    /**
     * API地址
     */
    @NotBlank(message = "API地址不能为空", groups = {AddGroup.class, EditGroup.class})
    private String apiUrl;

    /**
     * API密钥
     */
    @NotBlank(message = "API密钥不能为空", groups = {AddGroup.class, EditGroup.class})
    private String apiKey;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 模型列表
     */
    private String modelList;

    /**
     * 额外参数
     */
    private String extraParams;

    /**
     * 状态
     */
    private String status;

    /**
     * 是否默认
     */
    private Boolean isDefault;

    /**
     * 备注
     */
    private String remark;

}
