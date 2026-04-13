package com.ai.briefing.config;

import com.ai.briefing.model.*;
import com.ai.briefing.repository.BriefingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final BriefingRepository briefingRepository;

    @Override
    public void run(String... args) {
        if (briefingRepository.count() > 0) return;

        log.info("Seeding sample briefing data...");

        // ── Briefing 1: Technology (Published) ────────────────────────────────
        Briefing tech = Briefing.builder()
                .title("AI Weekly Digest — Week 15")
                .summary("Top developments in artificial intelligence, large language models, and machine learning research this week.")
                .status(BriefingStatus.PUBLISHED)
                .category(BriefingCategory.TECHNOLOGY)
                .createdBy("system")
                .build();

        BriefingItem t1 = BriefingItem.builder()
                .briefing(tech)
                .headline("GPT-5 Sets New Benchmark Records")
                .content("OpenAI's latest model achieves state-of-the-art performance on MMLU, HumanEval, and reasoning benchmarks.")
                .source("TechCrunch")
                .sourceUrl("https://techcrunch.com/ai")
                .priority(1)
                .build();

        BriefingItem t2 = BriefingItem.builder()
                .briefing(tech)
                .headline("Google DeepMind Releases Open-Source Reasoning Dataset")
                .content("A 50M-sample dataset designed to improve multi-step reasoning in language models is now publicly available.")
                .source("DeepMind Blog")
                .sourceUrl("https://deepmind.google/blog")
                .priority(2)
                .build();

        BriefingItem t3 = BriefingItem.builder()
                .briefing(tech)
                .headline("EU AI Act Enforcement Begins Q3 2025")
                .content("European regulators confirm timelines for full enforcement of the AI Act, affecting high-risk AI systems.")
                .source("Reuters")
                .sourceUrl("https://reuters.com/technology")
                .priority(3)
                .build();

        tech.setItems(List.of(t1, t2, t3));
        briefingRepository.save(tech);

        // ── Briefing 2: Finance (Draft) ───────────────────────────────────────
        Briefing finance = Briefing.builder()
                .title("Markets Morning Brief — April 2026")
                .summary("Pre-market analysis covering equities, crypto, commodities, and central bank signals.")
                .status(BriefingStatus.DRAFT)
                .category(BriefingCategory.FINANCE)
                .createdBy("analyst-bot")
                .build();

        BriefingItem f1 = BriefingItem.builder()
                .briefing(finance)
                .headline("Fed Holds Rates Steady; Signals Caution")
                .content("The Federal Reserve kept the federal funds rate unchanged at the April FOMC meeting, citing persistent inflation uncertainty.")
                .source("Bloomberg")
                .sourceUrl("https://bloomberg.com/markets")
                .priority(1)
                .build();

        BriefingItem f2 = BriefingItem.builder()
                .briefing(finance)
                .headline("Bitcoin Surpasses $95K on ETF Inflows")
                .content("Spot Bitcoin ETFs recorded their largest single-day inflow of 2026, pushing the asset above $95,000.")
                .source("CoinDesk")
                .sourceUrl("https://coindesk.com")
                .priority(2)
                .build();

        finance.setItems(List.of(f1, f2));
        briefingRepository.save(finance);

        // ── Briefing 3: Scheduled ─────────────────────────────────────────────
        Briefing health = Briefing.builder()
                .title("Health & Science Weekly")
                .summary("Breakthroughs in medicine, public health updates, and clinical trial results.")
                .status(BriefingStatus.SCHEDULED)
                .category(BriefingCategory.HEALTH)
                .scheduledAt(LocalDateTime.now().plusHours(2))
                .createdBy("health-desk")
                .build();

        BriefingItem h1 = BriefingItem.builder()
                .briefing(health)
                .headline("mRNA Cancer Vaccine Shows 90% Efficacy in Phase III Trial")
                .content("Moderna and MSD's personalised cancer vaccine demonstrated a significant reduction in recurrence for stage III melanoma patients.")
                .source("The Lancet")
                .sourceUrl("https://thelancet.com")
                .priority(1)
                .build();

        health.setItems(List.of(h1));
        briefingRepository.save(health);

        log.info("Sample data seeded: {} briefings created", 3);
    }
}
