package com.ai.briefing.repository;

import com.ai.briefing.model.BriefingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BriefingItemRepository extends JpaRepository<BriefingItem, Long> {

    List<BriefingItem> findByBriefingIdOrderByPriorityAsc(Long briefingId);

    void deleteByBriefingId(Long briefingId);
}
