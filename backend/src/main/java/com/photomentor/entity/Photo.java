package com.photomentor.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("photo")
public class Photo {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String url;
    
    private String thumbnailUrl;
    
    private String base64Data;
    
    private LocalDateTime createdAt;
}
