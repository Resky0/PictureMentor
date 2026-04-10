package com.photomentor.controller;

import com.photomentor.common.Result;
import com.photomentor.dto.ShootingAdviceRequest;
import com.photomentor.dto.ShootingAdviceResponse;
import com.photomentor.service.AIAnalysisService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guide")
@CrossOrigin(origins = "*")
public class GuideController {

    private final AIAnalysisService aiAnalysisService;

    public GuideController(AIAnalysisService aiAnalysisService) {
        this.aiAnalysisService = aiAnalysisService;
    }

    @PostMapping("/advice")
    public Result<ShootingAdviceResponse> getAdvice(@RequestBody ShootingAdviceRequest request) {
        try {
            ShootingAdviceResponse response = aiAnalysisService.getShootingAdvice(request.getImageData());
            return Result.success(response);
        } catch (Exception e) {
            return Result.error("获取建议失败: " + e.getMessage());
        }
    }
}
