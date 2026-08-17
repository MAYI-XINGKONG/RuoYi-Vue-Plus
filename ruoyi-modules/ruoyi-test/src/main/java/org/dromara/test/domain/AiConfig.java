package org.dromara.test.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI配置对象 ai_config
 *
 * @author autotest
 */
@Data
@TableName("ai_config")
public class AiConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 配置ID
     */
    @TableId(value = "config_id")
    private Long configId;

    /**
     * 配置名称
     */
    private String configName;

    /**
     * API地址
     */
    private String apiUrl;

    /**
     * API密钥
     */
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
     * 创建者
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    private Long updateBy;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    private String remark;

}
