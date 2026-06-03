package com.example.aiimagesearch.model;

public record VectorTaskDto(
        String taskId,
        String imageId,
        String taskType,
        String status,
        int retryCount,
        String updatedAt
) {
}
