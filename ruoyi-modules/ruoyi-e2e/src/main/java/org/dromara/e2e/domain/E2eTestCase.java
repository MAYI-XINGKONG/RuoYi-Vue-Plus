package org.dromara.e2e.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.io.Serial;

/**
 * E2E测试用例表 e2e_test_case
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("e2e_test_case")
public class E2eTestCase extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用例ID
     */
    @TableId(value = "case_id")
    private Long caseId;

    /**
     * 用例名称
     */
    private String caseName;

    /**
     * spec文件路径
     */
    private String specFile;

    /**
     * 用例分组
     */
    private String caseGroup;

    /**
     * 用例描述
     */
    private String description;

    /**
     * 用例代码内容
     */
    private String content;

    /**
     * 状态
     */
    private String status;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private String delFlag;
}
