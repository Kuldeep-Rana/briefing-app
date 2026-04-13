package com.ai.briefing.service;

import com.ai.briefing.dto.BriefingRequestDTO;
import com.ai.briefing.dto.BriefingResponseDTO;
import com.ai.briefing.model.BriefingCategory;
import com.ai.briefing.model.BriefingStatus;

import java.util.List;

public interface BriefingService {

    BriefingResponseDTO createBriefing(BriefingRequestDTO request);

    BriefingResponseDTO getBriefingById(Long id);

    List<BriefingResponseDTO> getAllBriefings();

    List<BriefingResponseDTO> getBriefingsByStatus(BriefingStatus status);

    List<BriefingResponseDTO> getBriefingsByCategory(BriefingCategory category);

    List<BriefingResponseDTO> searchBriefings(String keyword);

    BriefingResponseDTO updateBriefing(Long id, BriefingRequestDTO request);

    BriefingResponseDTO publishBriefing(Long id);

    BriefingResponseDTO archiveBriefing(Long id);

    void deleteBriefing(Long id);

    void publishScheduledBriefings();
}
