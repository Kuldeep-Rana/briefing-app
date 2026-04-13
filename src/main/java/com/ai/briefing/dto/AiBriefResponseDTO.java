package com.ai.briefing.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AiBriefResponseDTO {

    private String userEmail;
    private String userName;
    private LocalDateTime generatedAt;
    private List<String> topics;
    private List<TopicDigest> digests;
    private String overallSummary;

    @Data
    @Builder
    public static class TopicDigest {
        private String topic;
        private List<ArticleSummary> articles;
        private String topicSummary; // AI-generated 2-line overview of this topic
    }

    @Data
    @Builder
    public static class ArticleSummary {
        private String headline;
        private String summary;   // AI-generated 1-2 sentence summary
        private String source;
        private String url;
        private String publishedAt;
    }
}
