package com.photomentor.controller;

import com.photomentor.common.Result;
import com.photomentor.model.dto.ScoreRequest;
import com.photomentor.model.entity.Score;
import com.photomentor.service.ScoringService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/scoring")
public class ScoringController {

    private final ScoringService scoringService;

    public ScoringController(ScoringService scoringService) {
        this.scoringService = scoringService;
    }

    @PostMapping("/analyze")
    public Result<Score> analyze(@RequestBody ScoreRequest request, HttpServletRequest httpServletRequest) {
        try {
            Long userId = (Long) httpServletRequest.getAttribute("userId");
            Score score = scoringService.analyzeAndSave(request.getPhotoId(), userId);
            return Result.success(score);
        } catch (Exception e) {
            return Result.error("分析失败: " + e.getMessage());
        }
    }
}
