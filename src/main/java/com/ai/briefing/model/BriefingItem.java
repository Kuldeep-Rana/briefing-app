package com.ai.briefing.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "briefing_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BriefingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "briefing_id", nullable = false)
    @ToString.Exclude
    private Briefing briefing;

    @Column(nullable = false)
    private String headline;

    @Column(length = 2000)
    private String content;

    private String source;

    private String sourceUrl;

    private Integer priority;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
