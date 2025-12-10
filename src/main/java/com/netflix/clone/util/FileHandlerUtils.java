package com.netflix.clone.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.io.InputStreamResource;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

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

    public static long[] parseRangeHeader(String rangeHeader, long fileLength) {
        String[] ranges = rangeHeader.replace("bytes=", "").split("-");
        long rangeStart = Long.parseLong(ranges[0]);
        long rangeEnd = ranges.length > 1 && !ranges[1].isEmpty() ? Long.parseLong(ranges[1]) : fileLength - 1;
        return new long[] { rangeStart, rangeEnd };
    }

    public static Resource createRangeResource(Path filePath, long rangeStart, long rangeLength) throws IOException {
        RandomAccessFile fileReader = new RandomAccessFile(filePath.toFile(), "r");
        fileReader.seek(rangeStart);
        InputStream partialContentStream = new InputStream() {
            private long totalBytesRead = 0;

            @Override
            public int read() throws IOException {
                if (totalBytesRead >= rangeLength) {
                    fileReader.close();
                    return -1;
                }
                totalBytesRead++;
                return fileReader.read();
            }

            @Override
            public int read(byte[] buffer, int offset, int length) throws IOException {
                if (totalBytesRead >= rangeLength) {
                    fileReader.close();
                    return -1;
                }
                long remainingBytes = rangeLength - totalBytesRead;
                int bytesToRead = (int) Math.min(length, remainingBytes);
                int bytesActuallyRead = fileReader.read(buffer, offset, bytesToRead);
                if (bytesActuallyRead > 0) {
                    totalBytesRead += bytesActuallyRead;
                }
                if (totalBytesRead >= rangeLength) {
                    fileReader.close();
                }
                return bytesActuallyRead;
            }

            @Override
            public void close() throws IOException {
                fileReader.close();
            }
        };
        return new InputStreamResource(partialContentStream) {
            @Override
            public long contentLength() {
                return rangeLength;
            }
        };
    }

    public static Resource createFullResource(Path filePath) throws IOException {
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            throw new IOException("File not found or not readable: " + filePath);
        }
        return resource;
    }

}
