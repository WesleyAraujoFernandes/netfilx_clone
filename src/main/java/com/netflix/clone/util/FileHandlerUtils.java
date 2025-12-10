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

    public static String detectImageContentType(String filename) {
        if (filename == null) {
            return "image/jpeg"; // Default content type
        }

        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (filename.endsWith(".png")) {
            return "image/png";
        } else if (filename.endsWith(".gif")) {
            return "image/gif";
        } else if (filename.endsWith(".bmp")) {
            return "image/bmp";
        } else if (filename.endsWith(".webp")) {
            return "image/webp";
        } else if (filename.endsWith(".tiff") || filename.endsWith(".tif")) {
            return "image/tiff";
        } else if (filename.endsWith(".svg")) {
            return "image/svg+xml";
        } else {
            return "image/jpeg"; // Default content type
        }
    }

    public static String detectImageContentType(String filename) {
        if (filename == null) {
            return "image/jpeg"; // Default content type
        }

        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (filename.endsWith(".png")) {
            return "image/png";
        } else if (filename.endsWith(".gif")) {
            return "image/gif";
        } else if (filename.endsWith(".bmp")) {
            return "image/bmp";
        } else if (filename.endsWith(".webp")) {
            return "image/webp";
        } else if (filename.endsWith(".tiff") || filename.endsWith(".tif")) {
            return "image/tiff";
        } else if (filename.endsWith(".svg")) {
            return "image/svg+xml";
        } else {
            return "image/jpeg"; // Default content type
        }
    }

    public static long[] parseRangeHeader(String rangeHeader, long fileLength) {
        if (rangeHeader == null || !rangeHeader.startsWith("bytes=")) {
            return new long[] { 0, fileLength - 1 };
        }

        String rangeValue = rangeHeader.substring(6);
        String[] parts = rangeValue.split("-");
        long start = 0;
        long end = fileLength - 1;

        try {
            if (!parts[0].isEmpty()) {
                start = Long.parseLong(parts[0]);
            }
            if (parts.length > 1 && !parts[1].isEmpty()) {
                end = Long.parseLong(parts[1]);
            }
        } catch (NumberFormatException e) {
            return new long[] { 0, fileLength - 1 };
        }

        if (start > end || end >= fileLength) {
            return new long[] { 0, fileLength - 1 };
        }

        return new long[] { start, end };
    }
}
