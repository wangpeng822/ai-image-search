package com.example.aiimagesearch.service;

import com.example.aiimagesearch.model.ImageAssetDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MockVectorAgentClient implements VectorAgentClient {

    private final List<ImageAssetDto> fixtures = List.of(
            new ImageAssetDto("img_10001", "红色连衣裙商品图", 0.9135,
                    "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=900&q=80",
                    "synced", "image/jpeg", "2026-06-02 09:10:21"),
            new ImageAssetDto("img_10002", "复古红色外套", 0.8712,
                    "https://images.unsplash.com/photo-1529139574466-a303027c1d8b?auto=format&fit=crop&w=900&q=80",
                    "synced", "image/jpeg", "2026-06-02 09:13:44"),
            new ImageAssetDto("img_10003", "街拍相似风格", 0.8230,
                    "https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=900&q=80",
                    "synced", "image/jpeg", "2026-06-02 09:17:05"),
            new ImageAssetDto("img_10004", "待同步图片样本", 0.7421,
                    "https://images.unsplash.com/photo-1496747611176-843222e1e57c?auto=format&fit=crop&w=900&q=80",
                    "pending", "image/jpeg", "2026-06-02 09:21:30")
    );

    @Override
    public void submitIndexTask(String imageId, String tenantId, String userId, String ossUrl) {
        // Reserved for HTTP or MQ notification to Python ai-agent.
    }

    @Override
    public List<ImageAssetDto> searchByText(String tenantId, String userId, String keyword, int topK) {
        return fixtures.stream().limit(topK).toList();
    }

    @Override
    public List<ImageAssetDto> searchByImage(String tenantId, String userId, String temporaryOssUrl, int topK) {
        return fixtures.stream().sorted((left, right) -> right.imageId().compareTo(left.imageId())).limit(topK).toList();
    }
}
