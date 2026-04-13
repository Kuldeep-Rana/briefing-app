package com.ai.briefing.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BriefingItemResponseDTO {

    private Long id;
    private String headline;
    private String content;
    private String source;
    private String sourceUrl;
    private Integer priority;
    private LocalDateTime createdAt;
}
