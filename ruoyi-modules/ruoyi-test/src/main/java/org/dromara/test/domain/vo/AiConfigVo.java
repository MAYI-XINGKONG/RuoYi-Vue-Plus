package org.dromara.test.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.apache.fesod.sheet.annotation.ExcelIgnoreUnannotated;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.annotation.format.DateTimeFormat;
import org.dromara.test.domain.AiConfig;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI配置视图对象 ai_config
 *
 * @author autotest
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = AiConfig.class)
public class AiConfigVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 配置ID
     */
    @ExcelProperty(value = "配置ID")
    private Long configId;

    /**
     * 配置名称
     */
    @ExcelProperty(value = "配置名称")
    private String configName;

    /**
     * API地址
     */
    @ExcelProperty(value = "API地址")
    private String apiUrl;

    /**
     * API密钥
     */
    @ExcelProperty(value = "API密钥")
    private String apiKey;

    /**
     * 模型名称
     */
    @ExcelProperty(value = "模型名称")
    private String modelName;

    /**
     * 模型列表
     */
    @ExcelProperty(value = "模型列表")
    private String modelList;

    /**
     * 额外参数
     */
    @ExcelProperty(value = "额外参数")
    private String extraParams;

    /**
     * 状态
     */
    @ExcelProperty(value = "状态")
    private String status;

    /**
     * 是否默认
     */
    @ExcelProperty(value = "是否默认")
    private Boolean isDefault;

    /**
     * 创建时间
     */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;

}
