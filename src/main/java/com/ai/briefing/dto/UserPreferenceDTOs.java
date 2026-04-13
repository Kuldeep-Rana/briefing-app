package com.ai.briefing.dto;

import com.ai.briefing.model.UserPersona;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class UserPreferenceDTOs {

    @Data
    public static class Request {
        @NotBlank(message = "Name is required")
        private String name;

        @Email(message = "Valid email required")
        @NotBlank(message = "Email is required")
        private String email;

        // e.g. ["Stock market news", "AI tools", "Startup funding"]
        private List<String> topics;

        // e.g. ["09:00", "13:00", "17:00"]
        private List<String> deliveryTimes;

        private UserPersona persona;
    }

    @Data
    public static class Response {
        private Long id;
        private String name;
        private String email;
        private List<String> topics;
        private List<String> deliveryTimes;
        private UserPersona persona;
        private boolean active;
        private LocalDateTime createdAt;
    }
}
