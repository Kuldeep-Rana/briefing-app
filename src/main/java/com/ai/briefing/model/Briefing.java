package com.ai.briefing.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "briefings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Briefing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 5000)
    private String summary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BriefingStatus status;

    @Enumerated(EnumType.STRING)
    private BriefingCategory category;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime scheduledAt;

    private String createdBy;

    @OneToMany(mappedBy = "briefing", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<BriefingItem> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = BriefingStatus.DRAFT;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
