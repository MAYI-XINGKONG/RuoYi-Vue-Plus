package org.dromara.test.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.apache.fesod.sheet.annotation.ExcelIgnoreUnannotated;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.annotation.format.DateTimeFormat;
import org.dromara.test.domain.TestReport;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 测试报告视图对象 test_report
 *
 * @author autotest
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = TestReport.class)
public class TestReportVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 报告ID
     */
    @ExcelProperty(value = "报告ID")
    private Long reportId;

    /**
     * 执行ID
     */
    @ExcelProperty(value = "执行ID")
    private Long executionId;

    /**
     * 报告名称
     */
    @ExcelProperty(value = "报告名称")
    private String reportName;

    /**
     * 报告类型
     */
    @ExcelProperty(value = "报告类型")
    private String reportType;

    /**
     * 文件路径
     */
    @ExcelProperty(value = "文件路径")
    private String filePath;

    /**
     * 文件大小
     */
    @ExcelProperty(value = "文件大小")
    private Long fileSize;

    /**
     * 报告摘要
     */
    @ExcelProperty(value = "报告摘要")
    private String summary;

    /**
     * 创建时间
     */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "创建时间")
    private LocalDateTime createTime;

}
