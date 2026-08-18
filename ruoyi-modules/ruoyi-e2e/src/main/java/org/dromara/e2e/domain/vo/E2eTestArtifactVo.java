package org.dromara.e2e.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.e2e.domain.E2eTestArtifact;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * E2E测试产物视图对象 e2e_test_artifact
 */
@Data
@AutoMapper(target = E2eTestArtifact.class)
public class E2eTestArtifactVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 产物ID
     */
    private Long artifactId;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 用例名称
     */
    private String caseName;

    /**
     * 产物类型（screenshot/video/trace）
     */
    private String artifactType;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
