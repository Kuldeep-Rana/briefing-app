package com.ai.briefing.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "briefing_id")
    private Briefing briefing;

    private String deliveryChannel; // EMAIL, WEBHOOK, CONSOLE

    private String status;          // SUCCESS, FAILED

    private String errorMessage;

    @Column(updatable = false)
    private LocalDateTime deliveredAt;

    @PrePersist
    protected void onCreate() {
        deliveredAt = LocalDateTime.now();
    }
}
