package com.ai.briefing.config;

import com.ai.briefing.model.*;
import com.ai.briefing.repository.BriefingRepository;
import com.ai.briefing.repository.UserPreferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final BriefingRepository briefingRepository;
    private final UserPreferenceRepository userPreferenceRepository;

    @Override
    public void run(String... args) {
        seedBriefings();
        seedUsers();
    }

    private void seedBriefings() {
        if (briefingRepository.count() > 0) return;
        log.info("Seeding sample briefings...");

        Briefing tech = Briefing.builder()
                .title("AI Weekly Digest — Week 15")
                .summary("Top developments in artificial intelligence and machine learning.")
                .status(BriefingStatus.PUBLISHED)
                .category(BriefingCategory.TECHNOLOGY)
                .createdBy("system")
                .build();

        BriefingItem t1 = BriefingItem.builder().briefing(tech)
                .headline("GPT-5 Sets New Benchmark Records")
                .content("OpenAI model achieves SOTA on MMLU and reasoning benchmarks.")
                .source("TechCrunch").sourceUrl("https://techcrunch.com/ai").priority(1).build();

        BriefingItem t2 = BriefingItem.builder().briefing(tech)
                .headline("EU AI Act Enforcement Begins Q3 2025")
                .content("Regulators confirm timelines for high-risk AI system compliance.")
                .source("Reuters").sourceUrl("https://reuters.com/technology").priority(2).build();

        tech.setItems(List.of(t1, t2));
        briefingRepository.save(tech);

        Briefing finance = Briefing.builder()
                .title("Markets Morning Brief — April 2026")
                .summary("Pre-market analysis: equities, crypto, commodities.")
                .status(BriefingStatus.DRAFT)
                .category(BriefingCategory.FINANCE)
                .createdBy("analyst-bot")
                .build();
        briefingRepository.save(finance);

        Briefing health = Briefing.builder()
                .title("Health & Science Weekly")
                .summary("Breakthroughs in medicine and clinical trials.")
                .status(BriefingStatus.SCHEDULED)
                .category(BriefingCategory.HEALTH)
                .scheduledAt(LocalDateTime.now().plusHours(2))
                .createdBy("health-desk")
                .build();
        briefingRepository.save(health);

        log.info("Seeded 3 sample briefings.");
    }

    private void seedUsers() {
        if (userPreferenceRepository.count() > 0) return;
        log.info("Seeding sample users...");

        userPreferenceRepository.save(UserPreference.builder()
                .name("Raj Mehta").email("raj@example.com")
                .topics("Stock market news,Cryptocurrency,Federal Reserve")
                .deliveryTimes("09:00,13:00,15:00")
                .persona(UserPersona.TRADER).active(true).build());

        userPreferenceRepository.save(UserPreference.builder()
                .name("Priya Sharma").email("priya@example.com")
                .topics("Startup funding,AI tools,Product launches")
                .deliveryTimes("08:00,18:00")
                .persona(UserPersona.FOUNDER).active(true).build());

        userPreferenceRepository.save(UserPreference.builder()
                .name("Arjun Singh").email("arjun@example.com")
                .topics("AI research,Science breakthroughs,Space exploration")
                .deliveryTimes("10:00")
                .persona(UserPersona.STUDENT).active(true).build());

        userPreferenceRepository.save(UserPreference.builder()
                .name("Sneha Patel").email("sneha@example.com")
                .topics("Cryptocurrency,DeFi,Blockchain regulation")
                .deliveryTimes("08:00,12:00,20:00")
                .persona(UserPersona.CRYPTO_USER).active(true).build());

        log.info("Seeded 4 sample users.");
    }
}
