package com.netflix.clone.util;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileHandlerUtils {
    private FileHandlerUtils() {
        // Private constructor to prevent instantiation
    }

    public static String extractFileExtension(String originalFileName) {
        if (originalFileName == null || originalFileName.lastIndexOf('.') == -1) {
            return "";
        }
        return originalFileName.substring(originalFileName.lastIndexOf('.') + 1);
    }

    public static Path findFileByUUID(Path directory, String uuid) throws Exception {
        return Files.list(directory)
                .filter(path -> path.getFileName().toString().startsWith(uuid + "."))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("File not found with UUID: " + uuid));
    }

    public static String detectVideoContentType(String filename) {
        if (filename == null) {
            return "video/mp4"; // Default content typ
        }

        if (filename.endsWith(".mp4")) {
            return "video/mp4";
        } else if (filename.endsWith(".avi")) {
            return "video/x-msvideo";
        } else if (filename.endsWith(".mov")) {
            return "video/quicktime";
        } else if (filename.endsWith(".wmv")) {
            return "video/x-ms-wmv";
        } else if (filename.endsWith(".flv")) {
            return "video/x-flv";
        } else if (filename.endsWith(".mkv")) {
            return "video/x-matroska";
        } else if (filename.endsWith(".webm")) {
            return "video/webm";
        } else if (filename.endsWith(".mpeg")) {
            return "video/mpeg";
        } else if (filename.endsWith(".3gp")) {
            return "video/3gpp";
        } else if (filename.endsWith(".ts")) {
            return "video/mp2t";
        } else if (filename.endsWith(".m4v")) {
            return "video/x-m4v";
        } else if (filename.endsWith(".f4v")) {
            return "video/x-f4v";
        } else if (filename.endsWith(".ogv")) {
            return "video/ogg";
        } else if (filename.endsWith(".rmvb")) {
            return "application/vnd.rn-realmedia-vbr";
        } else if (filename.endsWith(".vob")) {
            return "video/dvd";
        } else if (filename.endsWith(".divx")) {
            return "video/divx";
        } else if (filename.endsWith(".xvid")) {
            return "video/xvid";
        } else {
            return "video/mp4"; // Default content type
        }
    }
}
