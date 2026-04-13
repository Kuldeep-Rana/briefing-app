package com.ai.briefing.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "news_articles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(length = 5000)
    private String rawContent;

    @Column(length = 3000)
    private String aiSummary;

    private String source;

    @Column(length = 1000)
    private String url;

    private String topic;       // which topic this article belongs to

    private String imageUrl;

    private boolean summarized; // has AI processed it?

    @Column(nullable = false, updatable = false)
    private LocalDateTime fetchedAt;

    private LocalDateTime publishedAt;

    @PrePersist
    protected void onCreate() {
        fetchedAt = LocalDateTime.now();
    }
}
