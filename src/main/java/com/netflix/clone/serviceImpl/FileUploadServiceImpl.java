package com.netflix.clone.serviceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.netflix.clone.service.FileUploadService;
import com.netflix.clone.util.FileHandlerUtils;

import jakarta.annotation.PostConstruct;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    private Path videoStorageLocation;
    private Path imageStorageLocation;

    @Value("${file.upload.video-dir:uploads/videos}")
    private String videoDir;

    @Value("${file.upload.image-dir:uploads/images}")
    private String imageDir;

    @PostConstruct
    public void init() {
        this.videoStorageLocation = Paths.get(videoDir).toAbsolutePath().normalize();
        this.imageStorageLocation = Paths.get(imageDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.videoStorageLocation);
            Files.createDirectories(this.imageStorageLocation);
        } catch (Exception e) {
            throw new RuntimeException("Could not create the diretory where the uploaded files will be stored", e);
        }
    }

    @Override
    public String storeVideoFile(MultipartFile file) {
        return storeFile(file, videoStorageLocation);
    }

    @Override
    public String storeImageFile(MultipartFile file) {
        return storeFile(file, imageStorageLocation);
    }

    private String storeFile(MultipartFile file, Path storageLocation) {
        String fileExtension = FileHandlerUtils.extractFileExtension(file.getOriginalFilename());
        String uuid = UUID.randomUUID().toString();
        String filename = uuid + "." + fileExtension;

        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file: " + filename);
            }
            Path targetLocation = storageLocation.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return uuid;
        } catch (Exception e) {
            throw new RuntimeException("Failed to store file: " + filename, e);
        }
    }

    @Override
    public ResponseEntity<Resource> serveVideo(String uuid, String rangeHeader) {
        try {
            Path filePath = FileHandlerUtils.findFileByUUID(videoStorageLocation, uuid);
            Resource resource = FileHandlerUtils.createFullResource(filePath);
            String filename = resource.getFilename();
            String contentType = FileHandlerUtils.detectVideoContentType(filename);
            long fileLength = resource.contentLength();
            if (isFullContentRequest(rangeHeader)) {
                return buildFullVideoResponse(resource, contentType, filename, fileLength);
            }
            return buildPartialVideoResponse(filePath, rangeHeader, contentType, filename, fileLength);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private ResponseEntity<Resource> buildPartialVideoResponse(Path filePath, String rangeHeader, String contentType,
            String filename, long fileLength) throws IOException {
        long[] range = FileHandlerUtils.parseRangeHeader(rangeHeader, fileLength);
        long rangeStart = range[0];
        long rangeEnd = range[1];
        if (!isValidRange(rangeStart, rangeEnd, fileLength)) {
            return buildRangeNotSatisfiableResponse(fileLength);
        }
        long contentLength = rangeEnd - rangeStart + 1;
        Resource rangeReasource = FileHandlerUtils.createRangeResource(filePath, rangeStart, rangeEnd);
        return ResponseEntity.status(206)
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength))
                .header(HttpHeaders.CONTENT_RANGE,
                        "bytes " + rangeStart + "-" + rangeEnd + "/" + fileLength)
                .body(rangeReasource);
    }

    private ResponseEntity<Resource> buildRangeNotSatisfiableResponse(long fileLength) {
        return ResponseEntity.status(416)
                .header(HttpHeaders.CONTENT_RANGE, "bytes */" + fileLength)
                .build();
    }

    private boolean isValidRange(long rangeStart, long rangeEnd, long fileLength) {
        return rangeStart <= rangeEnd && rangeStart >= 0 && rangeEnd < fileLength;
    }

    private ResponseEntity<Resource> buildFullVideoResponse(Resource resource, String contentType, String filename,
            long fileLength) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename\"" + filename + "\"")
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileLength))
                .body(resource);
    }

    private boolean isFullContentRequest(String rangeHeader) {
        return rangeHeader == null || rangeHeader.isEmpty();
    }
}
