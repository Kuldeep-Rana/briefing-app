package com.ai.briefing.service;

import com.ai.briefing.model.NewsArticle;
import com.ai.briefing.repository.NewsArticleRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Fetches top news articles per topic from NewsAPI.org.
 *
 * Set in application.properties:
 *   briefing.news.api-key=YOUR_NEWSAPI_KEY
 *   briefing.news.enabled=true
 *
 * When disabled (default), falls back to realistic mock data for development.
 * Get a free key at https://newsapi.org
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NewsFetcherService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final NewsArticleRepository newsArticleRepository;

    @Value("${briefing.news.api-key:YOUR_NEWSAPI_KEY}")
    private String newsApiKey;

    @Value("${briefing.news.enabled:false}")
    private boolean newsApiEnabled;

    private static final String NEWS_API_URL = "https://newsapi.org/v2/everything";
    private static final int MAX_ARTICLES_PER_TOPIC = 5;

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Fetches top articles for a given topic keyword and persists them.
     * Returns live results (NewsAPI) or mock data depending on config.
     */
    public List<NewsArticle> fetchAndStoreArticles(String topic) {
        log.info("Fetching articles for topic: '{}'", topic);

        List<NewsArticle> articles = newsApiEnabled
                ? fetchFromNewsApi(topic)
                : generateMockArticles(topic);

        List<NewsArticle> saved = newsArticleRepository.saveAll(articles);
        log.info("Stored {} articles for topic '{}'", saved.size(), topic);
        return saved;
    }

    /**
     * Fetches articles for multiple topics (one call per topic).
     */
    public List<NewsArticle> fetchForTopics(List<String> topics) {
        List<NewsArticle> all = new ArrayList<>();
        for (String topic : topics) {
            try {
                all.addAll(fetchAndStoreArticles(topic));
            } catch (Exception ex) {
                log.error("Failed to fetch articles for topic '{}': {}", topic, ex.getMessage());
            }
        }
        return all;
    }

    // ── NewsAPI integration ───────────────────────────────────────────────────

    private List<NewsArticle> fetchFromNewsApi(String topic) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(NEWS_API_URL)
                    .queryParam("q", topic)
                    .queryParam("pageSize", MAX_ARTICLES_PER_TOPIC)
                    .queryParam("sortBy", "publishedAt")
                    .queryParam("language", "en")
                    .queryParam("apiKey", newsApiKey)
                    .toUriString();

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode articlesNode = root.path("articles");

            List<NewsArticle> result = new ArrayList<>();
            for (JsonNode a : articlesNode) {
                String title   = a.path("title").asText("");
                String content = a.path("content").asText(a.path("description").asText(""));
                String source  = a.path("source").path("name").asText("Unknown");
                String articleUrl = a.path("url").asText("");
                String imageUrl   = a.path("urlToImage").asText("");

                if (title.isBlank() || title.equals("[Removed]")) continue;

                result.add(NewsArticle.builder()
                        .title(title)
                        .rawContent(content)
                        .source(source)
                        .url(articleUrl)
                        .imageUrl(imageUrl)
                        .topic(topic)
                        .summarized(false)
                        .build());
            }
            return result;

        } catch (Exception ex) {
            log.error("NewsAPI call failed for topic '{}': {}", topic, ex.getMessage());
            return generateMockArticles(topic); // graceful degradation
        }
    }

    // ── Mock data for development / testing ───────────────────────────────────

    private List<NewsArticle> generateMockArticles(String topic) {
        Map<String, List<NewsArticle>> mockData = buildMockData();
        String matchedKey = mockData.keySet().stream()
                .filter(k -> topic.toLowerCase().contains(k.toLowerCase()))
                .findFirst()
                .orElse("general");

        return mockData.getOrDefault(matchedKey, mockData.get("general")).stream()
                .map(a -> a.toBuilder().topic(topic).build())
                .toList();
    }

    private Map<String, List<NewsArticle>> buildMockData() {
        return Map.of(
            "stock", List.of(
                mock("S&P 500 Hits New All-Time High Amid Tech Rally",
                     "The S&P 500 surpassed 5,800 points as mega-cap technology companies reported stronger-than-expected earnings, with Nvidia and Microsoft leading the charge.",
                     "Bloomberg", "https://bloomberg.com/markets"),
                mock("Fed Minutes Signal Two Rate Cuts Possible in 2025",
                     "Federal Reserve meeting minutes revealed growing consensus among officials that inflation is cooling fast enough to justify two 25bps cuts before year-end.",
                     "Reuters", "https://reuters.com/business/finance"),
                mock("Apple Beats Quarterly Estimates; Raises Dividend 10%",
                     "Apple Inc. posted $94B in quarterly revenue and announced a 10% increase to its quarterly dividend, citing robust services growth and iPhone 16 Pro demand.",
                     "CNBC", "https://cnbc.com/apple"),
                mock("Oil Slides to $72 on Surprise OPEC Production Increase",
                     "Brent crude fell 4% after OPEC+ unexpectedly raised output targets by 800,000 barrels per day, citing stable global demand and pressure from non-OPEC producers.",
                     "Financial Times", "https://ft.com/commodities"),
                mock("Goldman Sachs Raises 12-Month S&P Target to 6,200",
                     "Goldman's equity strategy team lifted its year-end S&P 500 target, citing falling yields, AI-driven productivity gains, and resilient corporate margins.",
                     "MarketWatch", "https://marketwatch.com/story/gs")
            ),
            "ai", List.of(
                mock("OpenAI Releases GPT-5 with Real-Time Web Browsing",
                     "GPT-5 debuts with native web access, advanced reasoning, and a new 'deep research' mode capable of producing 50-page reports autonomously.",
                     "TechCrunch", "https://techcrunch.com/ai/gpt5"),
                mock("Google DeepMind's Gemini Ultra 2 Tops Every Benchmark",
                     "Gemini Ultra 2 outperforms competing models on MMLU, HumanEval, and multimodal reasoning tasks, setting a new state-of-the-art across 12 benchmarks.",
                     "The Verge", "https://theverge.com/google"),
                mock("Meta Open-Sources Llama 4 with 400B Parameters",
                     "Meta AI released Llama 4, a 400-billion-parameter model under a permissive open-source license, immediately sparking fine-tuning activity across the research community.",
                     "Wired", "https://wired.com/meta-llama"),
                mock("EU AI Act Enforcement: First Fines Expected Q3 2025",
                     "European regulators confirmed that high-risk AI systems found non-compliant after June audits could face fines up to 3% of global annual revenue.",
                     "Reuters", "https://reuters.com/tech/eu-ai"),
                mock("Anthropic Raises $3B Series F at $50B Valuation",
                     "Anthropic closed a $3 billion funding round led by Google and Spark Capital, bringing its total valuation to $50B and funding Claude model development through 2026.",
                     "Bloomberg", "https://bloomberg.com/anthropic")
            ),
            "crypto", List.of(
                mock("Bitcoin Breaks $100K — What Happens Next?",
                     "Bitcoin crossed the psychological $100,000 mark for the first time, driven by ETF inflows exceeding $2B in a single week and growing institutional adoption.",
                     "CoinDesk", "https://coindesk.com/btc-100k"),
                mock("Ethereum Staking Yield Hits 5.8% Post-Dencun Upgrade",
                     "Ethereum's Dencun upgrade reduced L2 transaction fees by 90% and simultaneously increased validator staking yields, making ETH an attractive yield asset.",
                     "Decrypt", "https://decrypt.co/eth-dencun"),
                mock("SEC Approves First Spot Ethereum ETF Applications",
                     "The SEC granted approval to three spot Ethereum ETF applications from BlackRock, Fidelity, and VanEck, expected to unlock $10B in institutional capital.",
                     "The Block", "https://theblock.co/eth-etf"),
                mock("DeFi Total Value Locked Surpasses $200 Billion",
                     "Decentralized finance protocols collectively locked over $200B in assets for the first time, with Uniswap, Aave, and new liquid restaking protocols leading growth.",
                     "CoinTelegraph", "https://cointelegraph.com/defi"),
                mock("Solana Processes 100K TPS in Mainnet Stress Test",
                     "Solana's network sustained 100,000 transactions per second during a live mainnet benchmark, with average fees remaining under $0.001 per transaction.",
                     "CoinDesk", "https://coindesk.com/solana")
            ),
            "startup", List.of(
                mock("Y Combinator W25 Batch: 40% of Companies Are AI-Native",
                     "YC's Winter 2025 batch shows a record share of AI-first startups, with verticals spanning legal automation, drug discovery, and enterprise data infrastructure.",
                     "TechCrunch", "https://techcrunch.com/yc-w25"),
                mock("Andreessen Horowitz Launches $7.2B AI Infrastructure Fund",
                     "a16z announced its largest-ever fund targeting AI infrastructure, model training hardware, and developer tooling, signaling long-term conviction in the AI build-out.",
                     "Forbes", "https://forbes.com/a16z"),
                mock("Stripe Valued at $70B in Secondary Market Transactions",
                     "Secondary market trades of Stripe shares imply a $70B valuation, with investors anticipating a potential IPO in H2 2025 following three consecutive profitable quarters.",
                     "Bloomberg", "https://bloomberg.com/stripe"),
                mock("Remote Work Tool Notion Hits 50 Million Users",
                     "Notion crossed 50 million registered users and announced Notion AI Pro, a $20/month add-on with autonomous task management and deep codebase integration.",
                     "The Information", "https://theinformation.com/notion"),
                mock("SpaceX Starlink Raises $1B for Direct-to-Phone Service",
                     "Starlink secured $1B to accelerate its direct-to-cell satellite service, which enables standard smartphones to send texts from anywhere on Earth without a SIM.",
                     "CNBC", "https://cnbc.com/starlink")
            ),
            "general", List.of(
                mock("Climate Summit Reaches Historic Carbon Reduction Agreement",
                     "Over 140 nations signed the Geneva Climate Accord committing to 45% carbon reduction by 2035, backed by a $500B green transition fund.",
                     "BBC News", "https://bbc.com/climate"),
                mock("WHO Declares End to Three-Year Global Health Emergency",
                     "The World Health Organization formally ended the global emergency status for the respiratory virus that had affected 60+ countries since 2022.",
                     "Reuters", "https://reuters.com/health/who"),
                mock("James Webb Telescope Detects Signs of Water on Exoplanet",
                     "NASA's JWST confirmed spectroscopic evidence of water vapor and possible liquid water clouds in the atmosphere of exoplanet K2-18b, 120 light-years away.",
                     "NASA Science", "https://science.nasa.gov/webb"),
                mock("Global Literacy Rate Reaches All-Time High of 91%",
                     "UNESCO's annual report confirmed global adult literacy hit 91%, crediting mobile learning platforms and government programs in South Asia and Sub-Saharan Africa.",
                     "The Guardian", "https://theguardian.com/education"),
                mock("Electric Vehicle Sales Surpass Petrol Cars in Europe for First Time",
                     "EV sales in the EU outpaced internal combustion engine cars for the first time in a single quarter, led by Norway (98%), Netherlands (72%), and Germany (48%).",
                     "Reuters", "https://reuters.com/autos")
            )
        );
    }

    private NewsArticle mock(String title, String content, String source, String url) {
        return NewsArticle.builder()
                .title(title)
                .rawContent(content)
                .source(source)
                .url(url)
                .summarized(false)
                .build();
    }
}
