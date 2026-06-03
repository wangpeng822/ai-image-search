package com.example.aiimagesearch.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OssConfig {
    @Value("")
    private String endpoint;

    @Value("")
    private String accessKeyId;

    @Value("")
    private String accessKeySecret;

    public OSS ossClient(){
        return new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
    }
}
