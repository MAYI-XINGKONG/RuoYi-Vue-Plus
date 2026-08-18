package org.dromara.e2e.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.e2e.domain.E2eTestCaseHistory;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用例历史版本视图对象 e2e_test_case_history
 */
@Data
@AutoMapper(target = E2eTestCaseHistory.class)
public class E2eTestCaseHistoryVo implements Serializable {

    /**
     * 历史ID
     */
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
