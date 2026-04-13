package com.ai.briefing.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user_preferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String name;

    // Comma-separated topics: "Stock market news,AI tools,Startup funding"
    @Column(length = 1000)
    private String topics;

    // Comma-separated delivery times in HH:mm: "09:00,13:00,17:00"
    @Column(length = 200)
    private String deliveryTimes;

    private boolean active;

    @Enumerated(EnumType.STRING)
    private UserPersona persona; // TRADER, FOUNDER, STUDENT, CRYPTO_USER, GENERAL

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (active == false) active = true;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper: split topics into a list
    @Transient
    public List<String> getTopicList() {
        if (topics == null || topics.isBlank()) return List.of();
        return List.of(topics.split(","));
    }

    // Helper: split delivery times into a list
    @Transient
    public List<String> getDeliveryTimeList() {
        if (deliveryTimes == null || deliveryTimes.isBlank()) return List.of("09:00");
        return List.of(deliveryTimes.split(","));
    }
}
