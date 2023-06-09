package com.atguigu.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
/**
 * @author wangy
 */
@Data
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;
}
