package com.bgasol.plugin.minio.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Minio配置
 */
@Configuration
@Data
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {

    /**
     * 存储桶名称
     */
    public String bucket = "default";

    /**
     * Minio地址
     */
    private String endpoint;

    /**
     * 访问密钥
     */
    private String accessKey;

    /**
     * 密钥
     */
    private String secretKey;

    /**
     * 注入Minio客户端
     */
    @Bean
    public MinioClient minioClient() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        // 创建MinIO的Java客户端
        MinioClient client = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
        // 检查存储桶是否已经存在
        BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(this.bucket).build();
        if (!client.bucketExists(bucketExistsArgs)) {
            // 如果不存在就创建一个存储桶
            MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder().bucket(this.bucket).build();
            client.makeBucket(makeBucketArgs);
        }
        return client;
    }
}
