package org.dromara.e2e.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.e2e.domain.E2eTestCase;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * E2E测试用例视图对象 e2e_test_case
 */
@Data
@AutoMapper(target = E2eTestCase.class)
public class E2eTestCaseVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用例ID
     */
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
}
