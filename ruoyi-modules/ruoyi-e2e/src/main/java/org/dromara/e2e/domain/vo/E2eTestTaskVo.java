package org.dromara.e2e.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.e2e.domain.E2eTestTask;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * E2E测试任务视图对象 e2e_test_task
 */
@Data
@AutoMapper(target = E2eTestTask.class)
public class E2eTestTaskVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 是否有头模式（0无头 1有头）
     */
    private String headed;

    /**
     * 状态（0待执行 1执行中 2已完成 3失败 4已停止）
     */
    private String status;

    /**
     * 进程ID
     */
    private Long processId;

    /**
     * 总用例数
     */
    private Integer totalCases;

    /**
     * 通过数
     */
    private Integer passedCases;

    /**
     * 失败数
     */
    private Integer failedCases;

    /**
     * 跳过数
     */
    private Integer skippedCases;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 报告路径
     */
    private String reportPath;

    /**
     * 结果JSON路径
     */
    private String resultJsonPath;

    /**
     * 错误信息
     */
    private String errorMsg;

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
