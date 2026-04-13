package com.ai.briefing.repository;

import com.ai.briefing.model.NewsArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long> {
    List<NewsArticle> findByTopic(String topic);
    List<NewsArticle> findBySummarizedFalse();
    List<NewsArticle> findByTopicAndFetchedAtAfterOrderByFetchedAtDesc(String topic, LocalDateTime since);
    List<NewsArticle> findTop5ByTopicOrderByFetchedAtDesc(String topic);
}
