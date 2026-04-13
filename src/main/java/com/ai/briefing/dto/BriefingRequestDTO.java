package com.ai.briefing.dto;

import com.ai.briefing.model.BriefingCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BriefingRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @Size(max = 5000, message = "Summary must not exceed 5000 characters")
    private String summary;

    private BriefingCategory category;

    private LocalDateTime scheduledAt;

    private String createdBy;

    @Valid
    private List<BriefingItemRequestDTO> items;
}
