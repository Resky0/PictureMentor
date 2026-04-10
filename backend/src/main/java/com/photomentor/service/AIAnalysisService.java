package com.photomentor.service;

import com.photomentor.dto.ShootingAdviceResponse;
import com.photomentor.entity.Score;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
public class AIAnalysisService {

    private final Random random = new Random();

    public Score analyzePhoto(Long photoId, Long userId) {
        Score score = new Score();
        score.setPhotoId(photoId);
        score.setUserId(userId);
        
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
        response.setComposition("使用三分法则，将主体放在交叉点上");
        response.setExposure("当前光线适中，建议使用ISO 100-400");
        response.setFocus("将焦点对准人物眼睛");
        
        List<String> tips = Arrays.asList(
            "尝试不同的角度拍摄",
            "注意背景的选择，避免杂乱",
            "可以使用前景增加层次感",
            "适当使用道具增强画面氛围"
        );
        response.setTips(tips);
        
        return response;
    }
}
