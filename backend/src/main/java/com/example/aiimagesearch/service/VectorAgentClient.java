package com.example.aiimagesearch.service;

import com.example.aiimagesearch.model.ImageAssetDto;

import java.util.List;

public interface VectorAgentClient {

    void submitIndexTask(String imageId, String tenantId, String userId, String ossUrl);

    List<ImageAssetDto> searchByText(String tenantId, String userId, String keyword, int topK);

    List<ImageAssetDto> searchByImage(String tenantId, String userId, String temporaryOssUrl, int topK);
}
