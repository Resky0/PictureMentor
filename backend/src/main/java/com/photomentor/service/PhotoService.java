package com.photomentor.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.photomentor.entity.Photo;
import com.photomentor.mapper.PhotoMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Service
public class PhotoService extends ServiceImpl<PhotoMapper, Photo> {

    private final FileStorageService fileStorageService;

    public PhotoService(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    public Photo uploadPhoto(MultipartFile file, Long userId) throws IOException {
        String url = fileStorageService.storeFile(file);
        String base64Data = getPhotoAsBase64(file);
        
        Photo photo = new Photo();
        photo.setUserId(userId);
        photo.setUrl(url);
        photo.setThumbnailUrl(url);
        photo.setBase64Data(base64Data);
        
        save(photo);
        return photo;
    }

    public String getPhotoAsBase64(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }
}
