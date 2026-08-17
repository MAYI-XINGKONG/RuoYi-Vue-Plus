package org.dromara.test.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.apache.fesod.sheet.annotation.ExcelIgnoreUnannotated;
import org.apache.fesod.sheet.annotation.ExcelProperty;
import org.apache.fesod.sheet.annotation.format.DateTimeFormat;
import org.dromara.test.domain.TestSuite;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 测试套件视图对象 test_suite
 *
 * @author autotest
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = TestSuite.class)
public class TestSuiteVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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
     * 套件类型
     */
    @ExcelProperty(value = "套件类型")
    private String suiteType;

    /**
     * 请求方法
     */
    @ExcelProperty(value = "请求方法")
    private String method;

    /**
     * 请求URL
     */
    @ExcelProperty(value = "请求URL")
    private String url;

    /**
     * 请求头
     */
    private String headers;

    /**
     * 请求体
     */
    private String body;

    /**
     * 期望状态码
     */
    @ExcelProperty(value = "期望状态码")
    private Integer expectCode;

    /**
     * 期望响应体
     */
    private String expectBody;

    /**
     * 脚本路径
     */
    @ExcelProperty(value = "脚本路径")
    private String scriptPath;

    /**
     * 标签
     */
    @ExcelProperty(value = "标签")
    private String tags;

    /**
     * 状态
     */
    @ExcelProperty(value = "状态")
    private String status;

    /**
     * 排序号
     */
    @ExcelProperty(value = "排序号")
    private Integer orderNum;

    /**
     * 创建时间
     */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;

}
