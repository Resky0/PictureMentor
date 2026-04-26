package com.photomentor.service;

import com.photomentor.config.OpenAIConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenAIService {

    private final OpenAIConfig openAIConfig;
    private final RestTemplate restTemplate;

    public OpenAIService(OpenAIConfig openAIConfig) {
        this.openAIConfig = openAIConfig;
        this.restTemplate = new RestTemplate();
    }

    public Map<String, Object> analyzeImage(String imageBase64, String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAIConfig.getApiKey());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", openAIConfig.getModel());
        requestBody.put("max_tokens", 1000);

        List<Map<String, Object>> messages = new ArrayList<>();
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");

        List<Map<String, Object>> content = new ArrayList<>();
        Map<String, Object> textContent = new HashMap<>();
        textContent.put("type", "text");
        textContent.put("text", prompt);
        content.add(textContent);

        Map<String, Object> imageContent = new HashMap<>();
        imageContent.put("type", "image_url");
        Map<String, String> imageUrl = new HashMap<>();
        imageUrl.put("url", "data:image/jpeg;base64," + imageBase64);
        imageContent.put("image_url", imageUrl);
        content.add(imageContent);

        message.put("content", content);
        messages.add(message);
        requestBody.put("messages", messages);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                openAIConfig.getEndpoint(),
                HttpMethod.POST,
                entity,
                Map.class
        );

        return response.getBody();
    }

    public Map<String, Object> getShootingAdvice(String imageBase64) {
        String prompt = "你是一位专业的摄影顾问，请分析这张照片的构图、光线、色彩和焦点，并提供详细的拍摄建议。请按照以下格式返回：\n" +
                "1. 构图分析和建议\n" +
                "2. 光线分析和建议\n" +
                "3. 色彩分析和建议\n" +
                "4. 焦点分析和建议\n" +
                "5. 具体的改进建议列表\n" +
                "请提供专业、详细且实用的建议。";
        return analyzeImage(imageBase64, prompt);
    }

    public Map<String, Object> analyzePhotoForScoring(String imageBase64) {
        String prompt = "你是一位专业的摄影评委，请对这张照片进行评分。评分标准如下：\n" +
                "1. 构图：0-100分\n" +
                "2. 光线：0-100分\n" +
                "3. 色彩：0-100分\n" +
                "4. 焦点：0-100分\n" +
                "请给出每个维度的具体分数，并计算总分。同时提供详细的分析和改进建议。\n" +
                "请按照以下JSON格式返回：\n" +
                "{\n" +
                "  \"compositionScore\": 85,\n" +
                "  \"lightingScore\": 90,\n" +
                "  \"colorScore\": 80,\n" +
                "  \"focusScore\": 95,\n" +
                "  \"totalScore\": 87.5,\n" +
                "  \"analysis\": \"详细分析...\",\n" +
                "  \"suggestions\": [\"建议1\", \"建议2\", \"建议3\"]\n" +
                "}";
        return analyzeImage(imageBase64, prompt);
    }
}