package org.dromara.e2e.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.oss.client.OssClient;
import org.dromara.common.oss.config.OssClientConfig;
import org.dromara.common.oss.factory.OssFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.BucketAlreadyExistsException;
import software.amazon.awssdk.services.s3.model.BucketAlreadyOwnedByYouException;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.PutBucketPolicyRequest;

import java.net.URI;

/**
 * 启动时自动确保MinIO桶存在且为公开读取策略
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class E2eOssInitializer implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        try {
            OssClient client = OssFactory.instance();
            OssClientConfig config = client.config();

            String bucket = config.bucket().orElse("e2e-test");
            String endpoint = config.getEndpointUrl();
            String accessKey = config.accessKey().orElse("");
            String secretKey = config.secretKey().orElse("");
            boolean usePathStyle = config.usePathStyleAccess();

            if (accessKey.isBlank() || secretKey.isBlank()) {
                log.warn("MinIO accessKey/secretKey 未配置，跳过桶策略设置");
                return;
            }

            // 创建同步S3客户端用于管理操作
            S3Client s3Client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKey, secretKey)))
                .region(Region.US_EAST_1)
                .forcePathStyle(usePathStyle)
                .build();

            // 确保桶存在
            try {
                s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
                log.info("MinIO 桶已创建: {}", bucket);
            } catch (BucketAlreadyExistsException | BucketAlreadyOwnedByYouException e) {
                log.debug("MinIO 桶已存在: {}", bucket);
            }

            // 设置桶策略为公开读取
            String policy = "{\"Version\":\"2012-10-17\",\"Statement\":["
                + "{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},"
                + "\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucket\",\"s3:GetObject\"],"
                + "\"Resource\":[\"arn:aws:s3:::" + bucket + "\",\"arn:aws:s3:::" + bucket + "/*\"]"
                + "}]}";

            s3Client.putBucketPolicy(PutBucketPolicyRequest.builder()
                .bucket(bucket)
                .policy(policy)
                .build());

            log.info("MinIO 桶策略已设置为公开读取: bucket={}", bucket);
            s3Client.close();

        } catch (Exception e) {
            log.warn("MinIO 桶策略自动设置失败（可手动设置）: {}", e.getMessage());
        }
    }
}
