package com.photomentor.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScoreVO {
    private Long id;
    private Long photoId;
    private Long userId;
    private Integer totalScore;
    private Integer compositionScore;
    private Integer lightingScore;
    private Integer colorScore;
    private Integer focusScore;
    private String suggestions;
    private String analysis;
    private LocalDateTime createdAt;
    private String photoUrl;
}
