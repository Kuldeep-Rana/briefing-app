package com.ai.briefing.service;

import com.ai.briefing.dto.AiBriefResponseDTO;
import com.ai.briefing.model.*;
import com.ai.briefing.repository.NewsArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Core orchestration service.
 *
 * Flow:
 * 1. Fetch articles for each user topic  (NewsFetcherService)
 * 2. AI-summarize each article           (AiSummarizerService)
 * 3. Generate per-topic digest summary   (AiSummarizerService)
 * 4. Generate overall briefing intro     (AiSummarizerService)
 * 5. Assemble → AiBriefResponseDTO       (returned / saved)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AiBriefingGeneratorService {

    private final NewsFetcherService newsFetcherService;
    private final AiSummarizerService aiSummarizerService;
    private final NewsArticleRepository newsArticleRepository;

    private static final DateTimeFormatter DISPLAY_FMT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    // ── Public ────────────────────────────────────────────────────────────────

    /**
     * Generates a full personalized briefing for a given user preference.
     */
    public AiBriefResponseDTO generateForUser(UserPreference userPref) {
        log.info("Generating briefing for user: {} | topics: {}",
                userPref.getEmail(), userPref.getTopics());

        List<String> topics = userPref.getTopicList();
        List<AiBriefResponseDTO.TopicDigest> digests = new ArrayList<>();

        for (String topic : topics) {
            AiBriefResponseDTO.TopicDigest digest = buildTopicDigest(topic, userPref.getPersona());
            digests.add(digest);
        }

        String overallSummary = aiSummarizerService.generateOverallSummary(topics, userPref.getPersona());

        return AiBriefResponseDTO.builder()
                .userEmail(userPref.getEmail())
                .userName(userPref.getName())
                .generatedAt(LocalDateTime.now())
                .topics(topics)
                .digests(digests)
                .overallSummary(overallSummary)
                .build();
    }

    /**
     * On-demand: regenerate briefing for a specific topic only.
     */
    public AiBriefResponseDTO.TopicDigest generateTopicDigestOnDemand(String topic, UserPersona persona) {
        return buildTopicDigest(topic, persona);
    }

    // ── Internal ──────────────────────────────────────────────────────────────

    private AiBriefResponseDTO.TopicDigest buildTopicDigest(String topic, UserPersona persona) {
        // 1. Fetch articles (live or mock)
        List<NewsArticle> articles = newsFetcherService.fetchAndStoreArticles(topic);

        // 2. AI-summarize each article
        List<AiBriefResponseDTO.ArticleSummary> articleSummaries = new ArrayList<>();
        for (NewsArticle article : articles) {
            String summary = aiSummarizerService.summarizeArticle(article);

            // Persist summary back
            article.setAiSummary(summary);
            article.setSummarized(true);
            newsArticleRepository.save(article);

            articleSummaries.add(AiBriefResponseDTO.ArticleSummary.builder()
                    .headline(article.getTitle())
                    .summary(summary)
                    .source(article.getSource())
                    .url(article.getUrl())
                    .publishedAt(article.getFetchedAt() != null
                            ? article.getFetchedAt().format(DISPLAY_FMT)
                            : LocalDateTime.now().format(DISPLAY_FMT))
                    .build());
        }

        // 3. Generate topic-level digest
        String topicSummary = aiSummarizerService.generateTopicDigest(topic, articles, persona);

        return AiBriefResponseDTO.TopicDigest.builder()
                .topic(topic)
                .articles(articleSummaries)
                .topicSummary(topicSummary)
                .build();
    }
}
