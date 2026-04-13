package com.ai.briefing.scheduler;

import com.ai.briefing.dto.AiBriefResponseDTO;
import com.ai.briefing.model.UserPreference;
import com.ai.briefing.repository.UserPreferenceRepository;
import com.ai.briefing.service.AiBriefingGeneratorService;
import com.ai.briefing.service.BriefingService;
import com.ai.briefing.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BriefingScheduler {

    private final BriefingService briefingService;
    private final UserPreferenceRepository userPreferenceRepository;
    private final AiBriefingGeneratorService generatorService;
    private final DeliveryService deliveryService;

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Runs every minute.
     * For each active user, checks if the current HH:mm matches any delivery time.
     * If yes → generate AI briefing and deliver it.
     */
    @Scheduled(fixedRate = 60_000)
    public void runDeliveryCheck() {
        String currentTime = LocalTime.now().format(TIME_FMT);
        log.debug("⏰ Scheduler tick — current time: {}", currentTime);

        List<UserPreference> activeUsers = userPreferenceRepository.findByActiveTrue();

        for (UserPreference user : activeUsers) {
            if (isDeliveryDue(user, currentTime)) {
                log.info("🔔 Delivery time hit for user: {} at {}", user.getEmail(), currentTime);
                try {
                    AiBriefResponseDTO brief = generatorService.generateForUser(user);
                    deliveryService.deliver(user, brief);
                } catch (Exception ex) {
                    log.error("Failed to generate/deliver for {}: {}", user.getEmail(), ex.getMessage(), ex);
                }
            }
        }
    }

    /**
     * Auto-publish SCHEDULED briefings whose scheduledAt time has passed.
     */
    @Scheduled(fixedRate = 60_000)
    public void publishScheduledBriefings() {
        try {
            briefingService.publishScheduledBriefings();
        } catch (Exception ex) {
            log.error("Error during scheduled briefing publish: {}", ex.getMessage(), ex);
        }
    }

    private boolean isDeliveryDue(UserPreference user, String currentTime) {
        return user.getDeliveryTimeList().stream()
                .anyMatch(t -> t.trim().equals(currentTime));
    }
}
