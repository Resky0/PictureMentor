package com.photomentor.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload.path}")
    private String uploadPath;

    public String storeFile(MultipartFile file) throws IOException {
        String extension = getFileExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID().toString() + "." + extension;
        
        Path path = Paths.get(uploadPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        
        Path filePath = path.resolve(filename);
        Files.copy(file.getInputStream(), filePath);
        
        return "/uploads/" + filename;
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "jpg";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
