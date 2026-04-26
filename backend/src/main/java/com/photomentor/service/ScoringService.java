package com.photomentor.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.photomentor.dto.ScoreVO;
import com.photomentor.entity.Photo;
import com.photomentor.entity.Score;
import com.photomentor.mapper.PhotoMapper;
import com.photomentor.mapper.ScoreMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ScoringService extends ServiceImpl<ScoreMapper, Score> {

    private final AIAnalysisService aiAnalysisService;
    private final PhotoMapper photoMapper;

    public ScoringService(AIAnalysisService aiAnalysisService, PhotoMapper photoMapper) {
        this.aiAnalysisService = aiAnalysisService;
        this.photoMapper = photoMapper;
    }

    @Transactional
    public Score analyzeAndSave(Long photoId, Long userId) {
        Score score = aiAnalysisService.analyzePhoto(photoId, userId);
        save(score);
        return score;
    }

    public Page<ScoreVO> getHistory(Long userId, int page, int size) {
        Page<Score> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Score> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Score::getUserId, userId);
        wrapper.orderByDesc(Score::getCreatedAt);
        Page<Score> scorePage = page(pageParam, wrapper);

        Set<Long> photoIds = scorePage.getRecords().stream()
                .map(Score::getPhotoId)
                .collect(Collectors.toSet());

        Map<Long, String> photoUrlMap = Map.of();
        if (!photoIds.isEmpty()) {
            List<Photo> photos = photoMapper.selectBatchIds(photoIds);
            photoUrlMap = photos.stream()
                    .collect(Collectors.toMap(Photo::getId, Photo::getUrl));
        }

        Page<ScoreVO> voPage = new Page<>(scorePage.getCurrent(), scorePage.getSize(), scorePage.getTotal());
        Map<Long, String> finalPhotoUrlMap = photoUrlMap;
        List<ScoreVO> voList = scorePage.getRecords().stream().map(score -> {
            ScoreVO vo = new ScoreVO();
            vo.setId(score.getId());
            vo.setPhotoId(score.getPhotoId());
            vo.setUserId(score.getUserId());
            vo.setTotalScore(score.getTotalScore());
            vo.setCompositionScore(score.getCompositionScore());
            vo.setLightingScore(score.getLightingScore());
            vo.setColorScore(score.getColorScore());
            vo.setFocusScore(score.getFocusScore());
            vo.setSuggestions(score.getSuggestions());
            vo.setAnalysis(score.getAnalysis());
            vo.setCreatedAt(score.getCreatedAt());
            vo.setPhotoUrl(finalPhotoUrlMap.getOrDefault(score.getPhotoId(), ""));
            return vo;
        }).collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }
}
