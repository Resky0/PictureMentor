package com.photomentor.model.vo;

import lombok.Data;
import java.util.List;

@Data
public class ShootingAdviceResponse {
    private String composition;
    private String exposure;
    private String focus;
    private List<String> tips;
}
