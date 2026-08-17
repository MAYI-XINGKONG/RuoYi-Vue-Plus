package org.dromara.test.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.apache.fesod.sheet.annotation.ExcelIgnoreUnannotated;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.annotation.format.DateTimeFormat;
import org.dromara.test.domain.TestExecution;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 测试执行视图对象 test_execution
 *
 * @author autotest
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = TestExecution.class)
public class TestExecutionVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 执行ID
     */
    @ExcelProperty(value = "执行ID")
    private Long executionId;

    /**
     * 套件ID
     */
    @ExcelProperty(value = "套件ID")
    private Long suiteId;

    /**
     * 套件名称
     */
    @ExcelProperty(value = "套件名称")
    private String suiteName;

    /**
     * 执行类型
     */
    @ExcelProperty(value = "执行类型")
    private String execType;

    /**
     * 浏览器
     */
    @ExcelProperty(value = "浏览器")
    private String browser;

    /**
     * 状态
     */
    @ExcelProperty(value = "状态")
    private String status;

    /**
     * 总数
     */
    @ExcelProperty(value = "总数")
    private Integer total;

    /**
     * 通过数
     */
    @ExcelProperty(value = "通过数")
    private Integer passed;

    /**
     * 失败数
     */
    @ExcelProperty(value = "失败数")
    private Integer failed;

    /**
     * 跳过数
     */
    @ExcelProperty(value = "跳过数")
    private Integer skipped;

    /**
     * 耗时(毫秒)
     */
    @ExcelProperty(value = "耗时(毫秒)")
    private Long duration;

    /**
     * 日志输出
     */
    private String logOutput;

    /**
     * 报告路径
     */
    @ExcelProperty(value = "报告路径")
    private String reportPath;

    /**
     * 开始时间
     */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "开始时间")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "结束时间")
    private LocalDateTime endTime;

    /**
     * 创建时间
     */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "创建时间")
    private LocalDateTime createTime;

}
