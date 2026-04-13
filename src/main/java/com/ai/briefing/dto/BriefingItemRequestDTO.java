package com.ai.briefing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BriefingItemRequestDTO {

    @NotBlank(message = "Headline is required")
    @Size(max = 255)
    private String headline;

    @Size(max = 2000)
    private String content;

    private String source;

    @Size(max = 500)
    private String sourceUrl;

    private Integer priority;
}
