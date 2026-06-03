package com.example.aiimagesearch.service;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Service
public class OssService {

    @Autowired(required = false)
    private OSS ossClient;

    @Value("")
    private String bucketName;

    public Map<String,String> generatePresignedUrl(String originalFilename){
        //1,生成唯一文件名
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String objectName = "uploads/" + UUID.randomUUID().toString() + suffix;
        //2,设置URL过期时间
        Date expiration = new Date(System.currentTimeMillis() + 500 * 60 * 10);
        //3,创建生成预签名URL的请求 必须指定为HttpMethod.PUT
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, objectName, HttpMethod.PUT);
        request.setExpiration(expiration);
        //4,让阿里云SDK计算生成带有签名加密参数的URL
        URL signeUrl = ossClient.generatePresignedUrl(request);

        //5,拼接无签名参数的,存储的文件最终访问路径(将来用于持久化存入数据库)
        String fileUrl  = signeUrl.getProtocol() + "://" + signeUrl.getHost() + "/" + objectName;

        //6.封装结果返回
        HashMap<String, String> result = new HashMap<>();
        result.put("uploadUrl",signeUrl.toString());
        result.put("fileUrl",fileUrl.toString());
        return result;
    }
}
