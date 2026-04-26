package com.photomentor.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.photomentor.entity.Photo;
import com.photomentor.entity.Score;
import com.photomentor.mapper.ScoreMapper;
import com.photomentor.service.PhotoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScoringService extends ServiceImpl<ScoreMapper, Score> {

    private final AIAnalysisService aiAnalysisService;

    private final PhotoService photoService;

    public ScoringService(AIAnalysisService aiAnalysisService, PhotoService photoService) {
        this.aiAnalysisService = aiAnalysisService;
        this.photoService = photoService;
    }

    @Transactional
    public Score analyzeAndSave(Long photoId, Long userId) {
        Photo photo = photoService.getById(photoId);
        String imageBase64 = photo != null ? photo.getBase64Data() : null;
        Score score = aiAnalysisService.analyzePhoto(photoId, userId, imageBase64);
        save(score);
        return score;
    }

    public Page<Score> getHistory(Long userId, int page, int size) {
        Page<Score> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Score> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Score::getUserId, userId);
        wrapper.orderByDesc(Score::getCreatedAt);
        return page(pageParam, wrapper);
    }
}
