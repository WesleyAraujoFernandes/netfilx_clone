package com.netflix.clone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.clone.dto.request.VideoRequest;
import com.netflix.clone.dto.response.MessageResponse;
import com.netflix.clone.dto.response.PageResponse;
import com.netflix.clone.dto.response.VideoResponse;
import com.netflix.clone.service.VideoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/videos")
public class VideoContoller {
    @Autowired
    private VideoService videoService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin")
    public ResponseEntity<MessageResponse> createVideoByAdmin(@Valid @RequestBody VideoRequest videoRequest) {
        return ResponseEntity.ok(videoService.createVideoByAdmin(videoRequest));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<PageResponse<VideoResponse>> getAllAdminVideos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(videoService.getAllAdminVideos(page, size, search));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{id}")
    public ResponseEntity<MessageResponse> updateVideoByAdmin(@PathVariable Long id,
            @Valid @RequestBody VideoRequest videoRequest) {
        return ResponseEntity.ok(videoService.updateVideoByAdmin(id, videoRequest));
    }
}
