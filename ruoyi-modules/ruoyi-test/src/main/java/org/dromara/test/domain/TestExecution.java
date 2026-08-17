package org.dromara.test.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 测试执行记录对象 test_execution
 *
 * @author autotest
 */
@Data
@TableName("test_execution")
public class TestExecution implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 执行ID
     */
    @TableId(value = "execution_id")
    private Long executionId;

    /**
     * 套件ID
     */
    private Long suiteId;

    /**
     * 执行类型
     */
    private String execType;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 状态
     */
    private String status;

    /**
     * 总数
     */
    private Integer total;

    /**
     * 通过数
     */
    private Integer passed;

    /**
     * 失败数
     */
    private Integer failed;

    /**
     * 跳过数
     */
    private Integer skipped;

    /**
     * 耗时(毫秒)
     */
    private Long duration;

    /**
     * 日志输出
     */
    private String logOutput;

    /**
     * 报告路径
     */
    private String reportPath;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 创建人
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
