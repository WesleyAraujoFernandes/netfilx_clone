package com.netflix.clone.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {

    String storeVideoFile(MultipartFile file);

    String storeImageFile(MultipartFile file);

    ResponseEntity<org.springframework.core.io.Resource> serveVideo(String uuid, String rangeHeader);

}
