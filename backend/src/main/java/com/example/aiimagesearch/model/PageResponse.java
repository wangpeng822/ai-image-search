package com.example.aiimagesearch.model;

import java.util.List;

public record PageResponse<T>(
        List<T> items,
        int total
) {
}
