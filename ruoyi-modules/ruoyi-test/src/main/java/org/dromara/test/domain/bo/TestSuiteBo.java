package org.dromara.test.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.test.domain.TestSuite;

import java.io.Serial;
import java.io.Serializable;

/**
 * 测试套件业务对象 test_suite
 *
 * @author autotest
 */
@Data
@AutoMapper(target = TestSuite.class, reverseConvertGenerate = false)
public class TestSuiteBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 套件ID
     */
    @NotNull(message = "套件ID不能为空", groups = {EditGroup.class})
    private Long suiteId;

    /**
     * 套件名称
     */
    @NotBlank(message = "套件名称不能为空", groups = {AddGroup.class, EditGroup.class})
    private String suiteName;

    /**
     * 套件类型
     */
    @NotBlank(message = "套件类型不能为空", groups = {AddGroup.class, EditGroup.class})
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
     * 备注
     */
    private String remark;

}
