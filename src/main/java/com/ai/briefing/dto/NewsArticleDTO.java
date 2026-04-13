package com.ai.briefing.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NewsArticleDTO {
    private Long id;
    private String title;
    private String rawContent;
    private String aiSummary;
    private String source;
    private String url;
    private String topic;
    private boolean summarized;
    private LocalDateTime fetchedAt;
}
