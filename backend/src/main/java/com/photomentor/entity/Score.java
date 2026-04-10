package com.photomentor.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("score")
public class Score {
    @TableId(type = IdType.AUTO)
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
}
