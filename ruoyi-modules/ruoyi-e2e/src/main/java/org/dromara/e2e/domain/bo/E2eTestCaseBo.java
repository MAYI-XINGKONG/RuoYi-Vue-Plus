package org.dromara.e2e.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.dromara.e2e.domain.E2eTestCase;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * E2E测试用例业务对象 e2e_test_case
 */
@Data
@AutoMapper(target = E2eTestCase.class, reverseConvertGenerate = false)
public class E2eTestCaseBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用例ID（更新时必填）
     */
    private Long caseId;

    /**
     * 用例名称
     */
    @NotBlank(message = "用例名称不能为空")
    @Size(max = 200, message = "用例名称长度不能超过{max}个字符")
    private String caseName;

    /**
     * spec文件路径
     */
    @Size(max = 500, message = "spec文件路径长度不能超过{max}个字符")
    private String specFile;

    /**
     * 用例分组
     */
    @Size(max = 100, message = "用例分组长度不能超过{max}个字符")
    private String caseGroup;

    /**
     * 用例描述
     */
    @Size(max = 500, message = "用例描述长度不能超过{max}个字符")
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
     * 请求参数
     */
    private Map<String, Object> params = new HashMap<>();
}
