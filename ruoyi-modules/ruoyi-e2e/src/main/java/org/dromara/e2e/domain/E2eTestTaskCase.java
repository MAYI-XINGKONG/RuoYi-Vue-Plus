package org.dromara.e2e.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * E2E测试任务用例关联表 e2e_test_task_case
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("e2e_test_task_case")
public class E2eTestTaskCase implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 用例ID
     */
    private Long caseId;
}
