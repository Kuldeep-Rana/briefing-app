package com.ai.briefing.scheduler;

import com.ai.briefing.service.BriefingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class BriefingScheduler {

    private final BriefingService briefingService;

    /**
     * Publishes any briefings whose scheduledAt time has passed.
     * Runs every minute by default; override with briefing.scheduler.cron in properties.
     */
    @Scheduled(fixedRateString = "${briefing.scheduler.fixed-rate:60000}")
    public void publishScheduledBriefings() {
        log.debug("Scheduler triggered at {}: checking for due briefings", LocalDateTime.now());
        try {
            briefingService.publishScheduledBriefings();
        } catch (Exception ex) {
            log.error("Error during scheduled briefing publish: {}", ex.getMessage(), ex);
        }
    }

    /**
     * Daily digest log — fires at 08:00 every day.
     */
    @Scheduled(cron = "${briefing.scheduler.cron:0 0 8 * * *}")
    public void dailyDigestLog() {
        log.info("Daily briefing digest check running at {}", LocalDateTime.now());
    }
}
