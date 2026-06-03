package com.example.aiimagesearch.controller;

import com.example.aiimagesearch.model.PageResponse;
import com.example.aiimagesearch.model.VectorTaskDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/vector-tasks")
public class VectorTaskController {

    @GetMapping
    public PageResponse<VectorTaskDto> listTasks() {
        List<VectorTaskDto> items = List.of(
                new VectorTaskDto("task_90001", "img_10001", "index", "success", 0, "2026-06-02 09:11:02"),
                new VectorTaskDto("task_90002", "img_10004", "index", "pending", 0, "2026-06-02 09:22:00"),
                new VectorTaskDto("task_90003", "img_09998", "reindex", "failed", 2, "2026-06-02 08:57:18")
        );
        return new PageResponse<>(items, items.size());
    }
}
