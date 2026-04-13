package com.ai.briefing.controller;

import com.ai.briefing.dto.AiBriefResponseDTO;
import com.ai.briefing.dto.ApiResponse;
import com.ai.briefing.model.DeliveryLog;
import com.ai.briefing.model.UserPersona;
import com.ai.briefing.model.UserPreference;
import com.ai.briefing.repository.DeliveryLogRepository;
import com.ai.briefing.repository.UserPreferenceRepository;
import com.ai.briefing.service.AiBriefingGeneratorService;
import com.ai.briefing.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/brief")
@RequiredArgsConstructor
@Tag(name = "AI Briefing", description = "On-demand AI briefing generation and delivery history")
public class AiBriefingController {

    private final AiBriefingGeneratorService generatorService;
    private final DeliveryService deliveryService;
    private final UserPreferenceRepository userPrefRepo;
    private final DeliveryLogRepository deliveryLogRepo;

    /**
     * Generate and deliver a briefing immediately for the given user email.
     * Useful for testing or manual triggers.
     */
    @PostMapping("/generate/{email}")
    @Operation(summary = "Generate & deliver briefing on-demand for a user")
    public ResponseEntity<ApiResponse<AiBriefResponseDTO>> generateForUser(
            @PathVariable String email) {

        UserPreference user = userPrefRepo.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("No subscriber found for: " + email));

        AiBriefResponseDTO brief = generatorService.generateForUser(user);
        deliveryService.deliver(user, brief);

        return ResponseEntity.ok(ApiResponse.success("Briefing generated and delivered", brief));
    }

    /**
     * Preview a briefing for a specific topic and persona without saving.
     */
    @GetMapping("/preview")
    @Operation(summary = "Preview a topic digest (no user required)",
               description = "Pass a topic keyword and persona to get a sample briefing. " +
                             "Useful for demos and testing.")
    public ResponseEntity<ApiResponse<AiBriefResponseDTO.TopicDigest>> previewTopic(
            @RequestParam String topic,
            @RequestParam(defaultValue = "GENERAL") UserPersona persona) {

        AiBriefResponseDTO.TopicDigest digest =
                generatorService.generateTopicDigestOnDemand(topic, persona);

        return ResponseEntity.ok(ApiResponse.success(digest));
    }

    /**
     * Recent delivery history (last 10 across all users).
     */
    @GetMapping("/history")
    @Operation(summary = "Get recent delivery history (last 10)")
    public ResponseEntity<ApiResponse<List<DeliveryLog>>> getRecentHistory() {
        return ResponseEntity.ok(
                ApiResponse.success(deliveryLogRepo.findTop10ByOrderByDeliveredAtDesc()));
    }

    /**
     * Delivery history for a specific user.
     */
    @GetMapping("/history/{email}")
    @Operation(summary = "Get delivery history for a specific user")
    public ResponseEntity<ApiResponse<List<DeliveryLog>>> getHistoryForUser(
            @PathVariable String email) {

        return ResponseEntity.ok(
                ApiResponse.success(deliveryLogRepo.findByUserEmailOrderByDeliveredAtDesc(email)));
    }
}
