package com.photomentor.controller;

import com.photomentor.common.Result;
import com.photomentor.entity.Photo;
import com.photomentor.service.PhotoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/photos")
@CrossOrigin(origins = "*")
public class PhotoController {

    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @PostMapping("/upload")
    public Result<Map<String, Object>> uploadPhoto(@RequestParam("file") MultipartFile file) {
        try {
            Long userId = 1L;
            Photo photo = photoService.uploadPhoto(file, userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("id", photo.getId());
            result.put("url", photo.getUrl());
            result.put("thumbnailUrl", photo.getThumbnailUrl());
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }
}
