package com.ai.briefing.dto;

import com.ai.briefing.model.BriefingCategory;
import com.ai.briefing.model.BriefingStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BriefingResponseDTO {

    private Long id;
    private String title;
    private String summary;
    private BriefingStatus status;
    private BriefingCategory category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime scheduledAt;
    private String createdBy;
    private List<BriefingItemResponseDTO> items;
    private int itemCount;
}
