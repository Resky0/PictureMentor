package com.photomentor.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.photomentor.model.entity.Photo;
import com.photomentor.mapper.PhotoMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class PhotoService extends ServiceImpl<PhotoMapper, Photo> {

    private final FileStorageService fileStorageService;

    public PhotoService(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    public Photo uploadPhoto(MultipartFile file, Long userId) throws IOException {
        String url = fileStorageService.storeFile(file);
        
        Photo photo = new Photo();
        photo.setUserId(userId);
        photo.setUrl(url);
        photo.setThumbnailUrl(url);
        
        save(photo);
        return photo;
    }
}
