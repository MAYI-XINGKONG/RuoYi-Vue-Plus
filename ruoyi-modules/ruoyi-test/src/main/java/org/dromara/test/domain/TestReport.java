package org.dromara.test.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 测试报告对象 test_report
 *
 * @author autotest
 */
@Data
@TableName("test_report")
public class TestReport implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 报告ID
     */
    @TableId(value = "report_id")
    private Long reportId;

    /**
     * 执行ID
     */
    private Long executionId;

    /**
     * 报告名称
     */
    private String reportName;

    /**
     * 报告类型
     */
    private String reportType;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 报告摘要
     */
    private String summary;

    /**
     * 创建人
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
