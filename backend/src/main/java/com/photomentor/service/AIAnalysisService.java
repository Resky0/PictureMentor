package com.photomentor.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.photomentor.dto.ShootingAdviceResponse;
import com.photomentor.entity.Photo;
import com.photomentor.entity.Score;
import com.photomentor.mapper.PhotoMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

@Service
public class AIAnalysisService {

    private static final String SCORING_MODEL = "qwen-vl-max-latest";

    private static final String SCORING_PROMPT = """
            你是一位专业的摄影评分师。请对这张照片从以下四个维度进行评分（0-100分）：
            1. 构图（composition）：画面布局、主体位置、视觉引导线、空间利用
            2. 光线（lighting）：光线运用、明暗对比、氛围营造、曝光控制
            3. 色彩（color）：色彩搭配、色调统一、视觉冲击力、白平衡
            4. 焦点（focus）：对焦准确度、景深控制、主体突出程度、清晰度

            评分标准：
            - 90-100：专业级，几乎无可挑剔
            - 75-89：优秀，有明显亮点，小有不足
            - 60-74：良好，基本合格，有提升空间
            - 40-59：一般，存在明显问题
            - 0-39：较差，需要大幅改进

            请严格按照以下JSON格式输出，不要输出任何其他内容：
            {
              "compositionScore": <0-100的整数>,
              "lightingScore": <0-100的整数>,
              "colorScore": <0-100的整数>,
              "focusScore": <0-100的整数>,
              "suggestions": ["具体的改进建议1", "具体的改进建议2", "具体的改进建议3", "具体的改进建议4"],
              "analysis": "50-100字的整体分析，指出照片的优点和不足"
            }
            """;

    private static final String SHOOTING_ADVICE_PROMPT = """
            你是一位专业的摄影指导师。请分析当前取景画面，给出专业的拍摄建议。

            请严格按照以下JSON格式输出，不要输出任何其他内容：
            {
              "composition": "构图建议，包括如何安排画面元素，使用什么构图法则",
              "exposure": "曝光建议，包括ISO、光圈、快门速度等参数建议",
              "focus": "对焦建议，包括对焦点选择和景深控制",
              "tips": ["实用拍摄技巧1", "实用拍摄技巧2", "实用拍摄技巧3", "实用拍摄技巧4"]
            }
            """;

    private final ChatClient chatClient;
    private final PhotoMapper photoMapper;
    private final ObjectMapper objectMapper;

    @Value("${file.upload.path}")
    private String uploadPath;

    public AIAnalysisService(ChatModel chatModel, PhotoMapper photoMapper, ObjectMapper objectMapper) {
        this.chatClient = ChatClient.builder(chatModel).build();
        this.photoMapper = photoMapper;
        this.objectMapper = objectMapper;
    }

    public Score analyzePhoto(Long photoId, Long userId) {
        Photo photo = photoMapper.selectById(photoId);
        if (photo == null) {
            throw new RuntimeException("照片不存在");
        }

        String filename = photo.getUrl().replace("/uploads/", "");
        Path imagePath = Paths.get(uploadPath, filename);
        if (!Files.exists(imagePath)) {
            throw new RuntimeException("照片文件不存在: " + filename);
        }

        Resource imageResource = new FileSystemResource(imagePath);
        String mimeType = determineMimeType(filename);

        Media media = new Media(MimeTypeUtils.parseMimeType(mimeType), imageResource);
        UserMessage userMessage = UserMessage.builder()
                .text(SCORING_PROMPT)
                .media(media)
                .build();

        Prompt prompt = new Prompt(userMessage, OpenAiChatOptions.builder()
                .model(SCORING_MODEL)
                .temperature(0.7)
                .build());

        String response = chatClient.prompt(prompt).call().content();

        try {
            String jsonStr = extractJson(response);
            JsonNode jsonNode = objectMapper.readTree(jsonStr);

            Score score = new Score();
            score.setPhotoId(photoId);
            score.setUserId(userId);
            score.setCompositionScore(clampScore(jsonNode.get("compositionScore").asInt()));
            score.setLightingScore(clampScore(jsonNode.get("lightingScore").asInt()));
            score.setColorScore(clampScore(jsonNode.get("colorScore").asInt()));
            score.setFocusScore(clampScore(jsonNode.get("focusScore").asInt()));
            score.setTotalScore((score.getCompositionScore() + score.getLightingScore()
                    + score.getColorScore() + score.getFocusScore()) / 4);
            score.setSuggestions(objectMapper.writeValueAsString(jsonNode.get("suggestions")));
            score.setAnalysis(jsonNode.get("analysis").asText());

            return score;
        } catch (Exception e) {
            throw new RuntimeException("AI分析结果解析失败: " + e.getMessage(), e);
        }
    }

    public ShootingAdviceResponse getShootingAdvice(String imageData) {
        byte[] imageBytes = decodeBase64Image(imageData);
        Resource imageResource = new ByteArrayResource(imageBytes);

        Media media = new Media(MimeTypeUtils.IMAGE_JPEG, imageResource);
        UserMessage userMessage = UserMessage.builder()
                .text(SHOOTING_ADVICE_PROMPT)
                .media(media)
                .build();

        Prompt prompt = new Prompt(userMessage, OpenAiChatOptions.builder()
                .model(SCORING_MODEL)
                .temperature(0.7)
                .build());

        String response = chatClient.prompt(prompt).call().content();

        try {
            String jsonStr = extractJson(response);
            JsonNode jsonNode = objectMapper.readTree(jsonStr);

            ShootingAdviceResponse advice = new ShootingAdviceResponse();
            advice.setComposition(jsonNode.get("composition").asText());
            advice.setExposure(jsonNode.get("exposure").asText());
            advice.setFocus(jsonNode.get("focus").asText());
            advice.setTips(objectMapper.convertValue(jsonNode.get("tips"),
                    new TypeReference<List<String>>() {}));

            return advice;
        } catch (Exception e) {
            throw new RuntimeException("AI拍摄建议解析失败: " + e.getMessage(), e);
        }
    }

    private String extractJson(String response) {
        String stripped = response.trim();
        if (stripped.startsWith("```json")) {
            stripped = stripped.substring(7);
        } else if (stripped.startsWith("```")) {
            stripped = stripped.substring(3);
        }
        if (stripped.endsWith("```")) {
            stripped = stripped.substring(0, stripped.length() - 3);
        }
        return stripped.trim();
    }

    private int clampScore(int score) {
        return Math.max(0, Math.min(100, score));
    }

    private String determineMimeType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".webp")) return "image/webp";
        return "image/jpeg";
    }

    private byte[] decodeBase64Image(String imageData) {
        String base64 = imageData;
        if (imageData.contains(",")) {
            base64 = imageData.substring(imageData.indexOf(",") + 1);
        }
        return Base64.getDecoder().decode(base64);
    }
}
