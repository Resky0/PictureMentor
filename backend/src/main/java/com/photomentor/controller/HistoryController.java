package com.photomentor.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.photomentor.common.Result;
import com.photomentor.entity.Score;
import com.photomentor.service.ScoringService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/history")
@CrossOrigin(origins = "*")
public class HistoryController {

    private final ScoringService scoringService;

    public HistoryController(ScoringService scoringService) {
        this.scoringService = scoringService;
    }

    @GetMapping
    public Result<Map<String, Object>> getHistory(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long userId = 1L;
            Page<Score> scorePage = scoringService.getHistory(userId, page, size);
            
            Map<String, Object> result = new HashMap<>();
            result.put("items", scorePage.getRecords());
            result.put("total", scorePage.getTotal());
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取历史记录失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Map<String, Boolean>> deleteHistory(@PathVariable Long id) {
        try {
            boolean deleted = scoringService.removeById(id);
            Map<String, Boolean> result = new HashMap<>();
            result.put("success", deleted);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("删除失败: " + e.getMessage());
        }
    }
}
