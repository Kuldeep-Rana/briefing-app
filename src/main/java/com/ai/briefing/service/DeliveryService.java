package com.ai.briefing.service;

import com.ai.briefing.dto.AiBriefResponseDTO;
import com.ai.briefing.model.DeliveryLog;
import com.ai.briefing.model.UserPreference;
import com.ai.briefing.repository.DeliveryLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Handles delivery of AI briefings.
 *
 * Currently delivers via console log.
 * Extend with:
 *  - Spring Mail (spring-boot-starter-mail) for email
 *  - WebClient for webhook/Slack delivery
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryLogRepository deliveryLogRepository;

    public void deliver(UserPreference user, AiBriefResponseDTO brief) {
        try {
            logToConsole(brief);
            persistDeliveryLog(user.getEmail(), "SUCCESS", null);
            log.info("✅ Briefing delivered to: {}", user.getEmail());
        } catch (Exception ex) {
            persistDeliveryLog(user.getEmail(), "FAILED", ex.getMessage());
            log.error("❌ Delivery failed for {}: {}", user.getEmail(), ex.getMessage());
        }
    }

    // ── Console Delivery (works out of the box) ───────────────────────────────

    private void logToConsole(AiBriefResponseDTO brief) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append("═".repeat(70)).append("\n");
        sb.append("  📰 DAILY AI BRIEFING — ").append(brief.getUserName())
          .append(" (").append(brief.getUserEmail()).append(")\n");
        sb.append("  🕐 ").append(brief.getGeneratedAt()).append("\n");
        sb.append("═".repeat(70)).append("\n\n");
        sb.append("  ").append(brief.getOverallSummary()).append("\n\n");

        for (AiBriefResponseDTO.TopicDigest digest : brief.getDigests()) {
            sb.append("  ┌─ 📌 ").append(digest.getTopic().toUpperCase()).append(" ─────────────────────────\n");
            sb.append("  │  ").append(digest.getTopicSummary()).append("\n");
            sb.append("  │\n");

            int i = 1;
            for (AiBriefResponseDTO.ArticleSummary article : digest.getArticles()) {
                sb.append("  │  ").append(i++).append(". ").append(article.getHeadline()).append("\n");
                sb.append("  │     ").append(article.getSummary()).append("\n");
                sb.append("  │     🔗 ").append(article.getSource())
                  .append(" — ").append(article.getUrl()).append("\n\n");
            }
            sb.append("  └─────────────────────────────────────────────────\n\n");
        }

        sb.append("═".repeat(70)).append("\n");
        log.info(sb.toString());
    }

    // ── Email Delivery (stub — wire up spring-boot-starter-mail) ─────────────

    @SuppressWarnings("unused")
    private void sendEmail(UserPreference user, AiBriefResponseDTO brief) {
        // Example with JavaMailSender:
        // SimpleMailMessage msg = new SimpleMailMessage();
        // msg.setTo(user.getEmail());
        // msg.setSubject("Your Daily AI Briefing — " + LocalDate.now());
        // msg.setText(formatAsPlainText(brief));
        // mailSender.send(msg);
        log.info("[EMAIL STUB] Would send to: {}", user.getEmail());
    }

    // ── Webhook Delivery (stub — fire to Slack/Discord/custom endpoint) ───────

    @SuppressWarnings("unused")
    private void sendWebhook(String webhookUrl, AiBriefResponseDTO brief) {
        // Use restTemplate.postForEntity(webhookUrl, brief, String.class)
        log.info("[WEBHOOK STUB] Would POST to: {}", webhookUrl);
    }

    // ── Persistence ───────────────────────────────────────────────────────────

    private void persistDeliveryLog(String email, String status, String error) {
        deliveryLogRepository.save(DeliveryLog.builder()
                .userEmail(email)
                .deliveryChannel("CONSOLE")
                .status(status)
                .errorMessage(error)
                .build());
    }
}
