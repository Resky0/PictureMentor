package com.photomentor.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.photomentor.common.Result;
import com.photomentor.dto.ScoreRequest;
import com.photomentor.entity.Score;
import com.photomentor.service.ScoringService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/scoring")
@CrossOrigin(origins = "*")
public class ScoringController {

    private final ScoringService scoringService;

    public ScoringController(ScoringService scoringService) {
        this.scoringService = scoringService;
    }

    @PostMapping("/analyze")
    public Result<Score> analyze(@RequestBody ScoreRequest request) {
        try {
            Long userId = 1L;
            Score score = scoringService.analyzeAndSave(request.getPhotoId(), userId);
            return Result.success(score);
        } catch (Exception e) {
            return Result.error("分析失败: " + e.getMessage());
        }
    }
}
