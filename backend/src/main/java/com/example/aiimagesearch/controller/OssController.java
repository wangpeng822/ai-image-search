package com.example.aiimagesearch.controller;

import com.example.aiimagesearch.service.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/oss")
public class OssController {
    @Autowired
    private OssService ossService;

    /**
     * 获取直传签名的接口
     */
    @GetMapping("/get-sign")
    public ResponseEntity<Map<String,String>> getSign(@RequestParam("filename") String filename){
        if (filename == null || filename.contains(".")){
            return ResponseEntity.badRequest().build();
        }
        Map<String, String> signData = ossService.generatePresignedUrl(filename);
        return ResponseEntity.ok(signData);
    }
}
