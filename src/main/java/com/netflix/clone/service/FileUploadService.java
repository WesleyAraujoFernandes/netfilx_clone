package com.netflix.clone.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;

public interface FileUploadService {

    String storeVideoFile(MultipartFile file);

    String storeImageFile(MultipartFile file);

    ResponseEntity<Resource> serveVideo(String uuid, String rangeHeader);

}
