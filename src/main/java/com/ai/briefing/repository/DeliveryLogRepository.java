package com.ai.briefing.repository;

import com.ai.briefing.model.DeliveryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryLogRepository extends JpaRepository<DeliveryLog, Long> {
    List<DeliveryLog> findByUserEmailOrderByDeliveredAtDesc(String userEmail);
    List<DeliveryLog> findTop10ByOrderByDeliveredAtDesc();
}
