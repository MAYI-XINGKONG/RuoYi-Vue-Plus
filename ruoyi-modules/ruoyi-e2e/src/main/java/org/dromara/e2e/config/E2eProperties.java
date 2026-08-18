package org.dromara.e2e.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * E2E测试配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "e2e")
public class E2eProperties {

    /**
     * E2E测试项目基础路径（支持相对路径，相对于应用工作目录）
     */
    private String basePath = "../e2e-test";

    /**
     * 最大并发任务数
     */
    private int maxConcurrency = 3;

    /**
     * 获取解析后的绝对路径
     * 尝试从多个基准目录解析，以适配不同的运行环境
     */
    public String getResolvedBasePath() {
        Path path = Paths.get(basePath);
        if (!path.isAbsolute()) {
            // 尝试多个基准目录
            Path userDir = Paths.get(System.getProperty("user.dir"));
            // 1. user.dir/e2e-test
            path = userDir.resolve(basePath).normalize();
            if (Files.exists(path)) {
                return path.toString();
            }
            // 2. user.dir/../e2e-test
            if (userDir.getParent() != null) {
                path = userDir.getParent().resolve(basePath).normalize();
                if (Files.exists(path)) {
                    return path.toString();
                }
            }
            // 3. user.dir/../../e2e-test
            if (userDir.getParent() != null && userDir.getParent().getParent() != null) {
                path = userDir.getParent().getParent().resolve(basePath).normalize();
                if (Files.exists(path)) {
                    return path.toString();
                }
            }
            // 最终回退到 user.dir/e2e-test（即使不存在，用于日志记录）
            path = userDir.resolve(basePath).normalize();
        }
        return path.toString();
    }
}
