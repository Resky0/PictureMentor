package com.photomentor.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.photomentor.entity.Score;
import com.photomentor.mapper.ScoreMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScoringService extends ServiceImpl<ScoreMapper, Score> {

    private final AIAnalysisService aiAnalysisService;

    public ScoringService(AIAnalysisService aiAnalysisService) {
        this.aiAnalysisService = aiAnalysisService;
    }

    @Transactional
    public Score analyzeAndSave(Long photoId, Long userId) {
        Score score = aiAnalysisService.analyzePhoto(photoId, userId);
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
