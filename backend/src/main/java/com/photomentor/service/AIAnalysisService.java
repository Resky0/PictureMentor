package com.photomentor.service;

import com.photomentor.dto.ShootingAdviceResponse;
import com.photomentor.entity.Score;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class AIAnalysisService {

    private final Random random = new Random();
    private final OpenAIService openAIService;
    private final ObjectMapper objectMapper;

    public AIAnalysisService(OpenAIService openAIService, ObjectMapper objectMapper) {
        this.openAIService = openAIService;
        this.objectMapper = objectMapper;
    }

    public Score analyzePhoto(Long photoId, Long userId, String imageBase64) {
        Score score = new Score();
        score.setPhotoId(photoId);
        score.setUserId(userId);
        
        try {
            // 使用AI大模型进行图像分析
            Map<String, Object> aiResult = openAIService.analyzePhotoForScoring(imageBase64);
            
            // 解析AI返回的结果
            if (aiResult != null && aiResult.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) aiResult.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    if (choice.containsKey("message")) {
                        Map<String, Object> message = (Map<String, Object>) choice.get("message");
                        if (message.containsKey("content")) {
                            String content = (String) message.get("content");
                            // 解析JSON格式的内容
                            Map<String, Object> analysisResult = objectMapper.readValue(content, Map.class);
                            
                            // 设置评分
                            score.setCompositionScore(((Number) analysisResult.get("compositionScore")).intValue());
                            score.setLightingScore(((Number) analysisResult.get("lightingScore")).intValue());
                            score.setColorScore(((Number) analysisResult.get("colorScore")).intValue());
                            score.setFocusScore(((Number) analysisResult.get("focusScore")).intValue());
                            score.setTotalScore(((Number) analysisResult.get("totalScore")).intValue());
                            
                            // 设置分析和建议
                            score.setAnalysis((String) analysisResult.get("analysis"));
                            List<String> suggestions = (List<String>) analysisResult.get("suggestions");
                            score.setSuggestions(objectMapper.writeValueAsString(suggestions));
                            
                            return score;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 降级方案：使用随机评分
        int composition = random.nextInt(30) + 70;
        int lighting = random.nextInt(30) + 70;
        int color = random.nextInt(30) + 70;
        int focus = random.nextInt(30) + 70;
        
        score.setCompositionScore(composition);
        score.setLightingScore(lighting);
        score.setColorScore(color);
        score.setFocusScore(focus);
        score.setTotalScore((composition + lighting + color + focus) / 4);
        
        String suggestions = "[\"注意构图，可以尝试三分法则\",\"光线利用得很好，继续保持\",\"色彩搭配可以更协调一些\",\"焦点清晰，主体突出\"]";
        score.setSuggestions(suggestions);
        score.setAnalysis("这是一张整体不错的照片，构图合理，光线运用恰当。建议在色彩搭配上可以进一步优化，让照片更加生动。");
        
        return score;
    }

    public ShootingAdviceResponse getShootingAdvice(String imageData) {
        ShootingAdviceResponse response = new ShootingAdviceResponse();
        
        try {
            // 使用AI大模型获取拍摄建议
            Map<String, Object> aiResult = openAIService.getShootingAdvice(imageData);
            
            // 解析AI返回的结果
            if (aiResult != null && aiResult.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) aiResult.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    if (choice.containsKey("message")) {
                        Map<String, Object> message = (Map<String, Object>) choice.get("message");
                        if (message.containsKey("content")) {
                            String content = (String) message.get("content");
                            // 解析内容并设置到响应中
                            String[] parts = content.split("\\n");
                            List<String> tips = new ArrayList<>();
                            
                            for (String part : parts) {
                                if (part.startsWith("1. 构图")) {
                                    response.setComposition(part.substring(5).trim());
                                } else if (part.startsWith("2. 光线")) {
                                    response.setExposure(part.substring(5).trim());
                                } else if (part.startsWith("3. 色彩")) {
                                    // 色彩建议可以添加到tips中
                                    tips.add(part.substring(5).trim());
                                } else if (part.startsWith("4. 焦点")) {
                                    response.setFocus(part.substring(5).trim());
                                } else if (part.startsWith("5. 具体的改进建议列表")) {
                                    // 后续行都是改进建议
                                    continue;
                                } else if (!part.isEmpty() && !part.startsWith("1.") && !part.startsWith("2.") && !part.startsWith("3.") && !part.startsWith("4.") && !part.startsWith("5.")) {
                                    tips.add(part.trim());
                                }
                            }
                            
                            response.setTips(tips);
                            return response;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 降级方案：使用默认建议
        response.setComposition("使用三分法则，将主体放在交叉点上");
        response.setExposure("当前光线适中，建议使用ISO 100-400");
        response.setFocus("将焦点对准人物眼睛");
        
        List<String> tips = new ArrayList<>();
        tips.add("尝试不同的角度拍摄");
        tips.add("注意背景的选择，避免杂乱");
        tips.add("可以使用前景增加层次感");
        tips.add("适当使用道具增强画面氛围");
        response.setTips(tips);
        
        return response;
    }
}
