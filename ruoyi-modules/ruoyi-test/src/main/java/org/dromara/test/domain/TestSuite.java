package org.dromara.test.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 测试套件对象 test_suite
 *
 * @author autotest
 */
@Data
@TableName("test_suite")
public class TestSuite implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 套件ID
     */
    @TableId(value = "suite_id")
    private Long suiteId;

    /**
     * 套件名称
     */
    private String suiteName;

    /**
     * 套件类型
     */
    private String suiteType;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求URL
     */
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
    private Integer expectCode;

    /**
     * 期望响应体
     */
    private String expectBody;

    /**
     * 脚本路径
     */
    private String scriptPath;

    /**
     * 标签
     */
    private String tags;

    /**
     * 状态
     */
    private String status;

    /**
     * 排序号
     */
    private Integer orderNum;

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

    /**
     * 备注
     */
    private String remark;

}
