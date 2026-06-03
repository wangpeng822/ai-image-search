package com.example.aiimagesearch.model;

public record ImageAssetDto(
        String imageId,
        String title,
        double score,
        String url,
        String vectorStatus,
        String contentType,
        String createdAt
) {
}
