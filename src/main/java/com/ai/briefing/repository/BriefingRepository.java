package com.ai.briefing.repository;

import com.ai.briefing.model.Briefing;
import com.ai.briefing.model.BriefingCategory;
import com.ai.briefing.model.BriefingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BriefingRepository extends JpaRepository<Briefing, Long> {

    List<Briefing> findByStatusOrderByCreatedAtDesc(BriefingStatus status);

    List<Briefing> findByCategoryOrderByCreatedAtDesc(BriefingCategory category);

    List<Briefing> findByStatusAndCategory(BriefingStatus status, BriefingCategory category);

    @Query("SELECT b FROM Briefing b WHERE b.scheduledAt <= :now AND b.status = 'SCHEDULED'")
    List<Briefing> findDueForPublishing(@Param("now") LocalDateTime now);

    @Query("SELECT b FROM Briefing b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(b.summary) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Briefing> searchByKeyword(@Param("keyword") String keyword);

    List<Briefing> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime from, LocalDateTime to);
}
