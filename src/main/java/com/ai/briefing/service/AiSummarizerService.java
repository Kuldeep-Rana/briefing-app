package com.ai.briefing.service;

import com.ai.briefing.model.NewsArticle;
import com.ai.briefing.model.UserPersona;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Calls an LLM API (OpenAI-compatible) to summarize news articles.
 *
 * Set these in application.properties:
 *   briefing.ai.api-url=https://api.openai.com/v1/chat/completions
 *   briefing.ai.api-key=sk-...
 *   briefing.ai.model=gpt-4o-mini
 *
 * Swap the URL/key for any OpenAI-compatible provider (Azure, Claude via proxy, Groq, etc.)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiSummarizerService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${briefing.ai.api-url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    @Value("${briefing.ai.api-key:YOUR_API_KEY}")
    private String apiKey;

    @Value("${briefing.ai.model:gpt-4o-mini}")
    private String model;

    @Value("${briefing.ai.enabled:false}")
    private boolean aiEnabled;

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Summarizes a single article into 2 crisp sentences.
     */
    public String summarizeArticle(NewsArticle article) {
        if (!aiEnabled) {
            return buildFallbackSummary(article.getTitle(), article.getRawContent());
        }

        String prompt = """
                You are a concise news summarizer. Summarize the following article in exactly 2 sentences.
                Be factual, remove fluff, and highlight the key insight.
                
                Title: %s
                Content: %s
                
                Summary (2 sentences only):
                """.formatted(article.getTitle(), truncate(article.getRawContent(), 1500));

        return callLlm(prompt, 150);
    }

    /**
     * Generates a topic-level digest — a 2-line overview of all articles under one topic.
     */
    public String generateTopicDigest(String topic, List<NewsArticle> articles, UserPersona persona) {
        if (!aiEnabled) {
            return "Top %d stories on '%s' today. Click each headline for the full story."
                    .formatted(articles.size(), topic);
        }

        StringBuilder headlines = new StringBuilder();
        for (int i = 0; i < articles.size(); i++) {
            headlines.append((i + 1)).append(". ").append(articles.get(i).getTitle()).append("\n");
        }

        String personaContext = getPersonaContext(persona);

        String prompt = """
                You are an AI briefing assistant. Write a 2-sentence overview of today's top stories
                on the topic "%s" for a %s.
                Be direct, insightful, and cut to what matters for them.
                
                Today's headlines:
                %s
                
                2-sentence overview:
                """.formatted(topic, personaContext, headlines);

        return callLlm(prompt, 120);
    }

    /**
     * Generates a master "overall summary" across all topics for the user.
     */
    public String generateOverallSummary(List<String> topics, UserPersona persona) {
        if (!aiEnabled) {
            return "Your personalized briefing covers: " + String.join(", ", topics) +
                   ". Stay informed and ahead of the curve!";
        }

        String prompt = """
                Write a 1-sentence motivational intro for a daily briefing email covering these topics: %s.
                It's for a %s. Make it crisp and relevant to their world.
                """.formatted(String.join(", ", topics), getPersonaContext(persona));

        return callLlm(prompt, 80);
    }

    // ── Internal ──────────────────────────────────────────────────────────────

    private String callLlm(String prompt, int maxTokens) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> body = Map.of(
                    "model", model,
                    "max_tokens", maxTokens,
                    "temperature", 0.4,
                    "messages", List.of(
                            Map.of("role", "user", "content", prompt)
                    )
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            return root.path("choices").get(0).path("message").path("content").asText().trim();

        } catch (Exception ex) {
            log.error("AI summarization failed: {}", ex.getMessage());
            return "Summary unavailable.";
        }
    }

    private String buildFallbackSummary(String title, String content) {
        // Simple heuristic fallback when AI is disabled
        if (content == null || content.isBlank()) return title;
        String firstSentence = content.split("[.!?]")[0].trim();
        return firstSentence.length() > 20 ? firstSentence + "." : title;
    }

    private String getPersonaContext(UserPersona persona) {
        if (persona == null) return "general reader";
        return switch (persona) {
            case TRADER       -> "stock trader who tracks markets and earnings";
            case FOUNDER      -> "startup founder focused on funding, growth, and tech trends";
            case STUDENT      -> "student interested in learning and research breakthroughs";
            case CRYPTO_USER  -> "crypto investor tracking prices, DeFi, and regulations";
            case GENERAL      -> "general reader who wants quick, clear news updates";
        };
    }

    private String truncate(String text, int maxChars) {
        if (text == null) return "";
        return text.length() > maxChars ? text.substring(0, maxChars) + "..." : text;
    }
}
