package com.example.aiimagesearch.controller;

import com.example.aiimagesearch.model.ImageAssetDto;
import com.example.aiimagesearch.model.PageResponse;
import com.example.aiimagesearch.model.SearchRequests.TextSearchRequest;
import com.example.aiimagesearch.service.VectorAgentClient;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final VectorAgentClient vectorAgentClient;

    public SearchController(VectorAgentClient vectorAgentClient) {
        this.vectorAgentClient = vectorAgentClient;
    }

    @PostMapping("/text")
    public PageResponse<ImageAssetDto> textSearch(@Valid @RequestBody TextSearchRequest request) {
        List<ImageAssetDto> items = vectorAgentClient.searchByText(
                "tenant_demo", "user_demo", request.keyword(), request.pageSize());
        return new PageResponse<>(items, items.size());
    }

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PageResponse<ImageAssetDto> imageSearch(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize
    ) {
        String temporaryOssUrl = file == null ? "oss://reserved/query.jpg" : "oss://reserved/tmp/" + file.getOriginalFilename();
        List<ImageAssetDto> items = vectorAgentClient.searchByImage("tenant_demo", "user_demo", temporaryOssUrl, pageSize);
        return new PageResponse<>(items, items.size());
    }
}
