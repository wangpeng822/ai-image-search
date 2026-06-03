package com.example.aiimagesearch.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public final class SearchRequests {

    private SearchRequests() {
    }

    public record TextSearchRequest(
            @NotBlank String keyword,
            @Min(1) int page,
            @Min(1) @Max(100) int pageSize
    ) {
    }
}
