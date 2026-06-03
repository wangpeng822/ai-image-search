package com.example.aiimagesearch.controller;

import com.example.aiimagesearch.model.ImageAssetDto;
import com.example.aiimagesearch.model.PageResponse;
import com.example.aiimagesearch.model.UploadResponse;
import com.example.aiimagesearch.service.VectorAgentClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final VectorAgentClient vectorAgentClient;

    public ImageController(VectorAgentClient vectorAgentClient) {
        this.vectorAgentClient = vectorAgentClient;
    }

    @GetMapping
    public PageResponse<ImageAssetDto> listImages() {
        List<ImageAssetDto> items = vectorAgentClient.searchByText("tenant_demo", "user_demo", "默认图片", 20);
        return new PageResponse<>(items, items.size());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadResponse uploadImage(@RequestPart("file") MultipartFile file) {
        String imageId = "img_" + Instant.now().toEpochMilli();
        vectorAgentClient.submitIndexTask(imageId, "tenant_demo", "user_demo", "oss://reserved/" + file.getOriginalFilename());
        return new UploadResponse(imageId, "pending");
    }
}
