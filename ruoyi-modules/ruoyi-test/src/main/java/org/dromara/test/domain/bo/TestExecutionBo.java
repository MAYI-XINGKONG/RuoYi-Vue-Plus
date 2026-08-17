package org.dromara.test.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.test.domain.TestExecution;

import java.io.Serial;
import java.io.Serializable;

/**
 * 测试执行业务对象 test_execution
 *
 * @author autotest
 */
@Data
@AutoMapper(target = TestExecution.class, reverseConvertGenerate = false)
public class TestExecutionBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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

}
