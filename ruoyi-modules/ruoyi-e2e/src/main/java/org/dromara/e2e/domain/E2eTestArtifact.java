package org.dromara.e2e.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * E2E测试产物表 e2e_test_artifact
 */
@Data
@TableName("e2e_test_artifact")
public class E2eTestArtifact implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 产物ID
     */
    @TableId(value = "artifact_id")
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
