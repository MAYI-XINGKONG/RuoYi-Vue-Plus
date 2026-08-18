package org.dromara.e2e.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.dromara.e2e.domain.E2eTestTask;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * E2E测试任务业务对象 e2e_test_task
 */
@Data
@AutoMapper(target = E2eTestTask.class, reverseConvertGenerate = false)
public class E2eTestTaskBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 任务名称
     */
    @NotBlank(message = "任务名称不能为空")
    private String taskName;

    /**
     * 浏览器类型
     */
    private String browser = "chromium";

    /**
     * 是否有头模式（0无头 1有头）
     */
    private String headed = "0";

    /**
     * 状态（0待执行 1执行中 2已完成 3失败 4已停止）
     */
    private String status;

    /**
     * 用例ID列表（用于创建任务时指定要执行的用例）
     */
    private List<Long> caseIds;

    /**
     * 请求参数
     */
    private Map<String, Object> params = new HashMap<>();
}
