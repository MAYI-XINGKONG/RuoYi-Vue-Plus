package org.dromara.e2e.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用例历史版本表 e2e_test_case_history
 */
@Data
@TableName("e2e_test_case_history")
public class E2eTestCaseHistory implements Serializable {

    /**
     * 历史ID
     */
    @TableId(value = "history_id")
    private Long historyId;

    /**
     * 用例ID
     */
    private Long caseId;

    /**
     * 用例代码内容
     */
    private String content;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 创建者
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
