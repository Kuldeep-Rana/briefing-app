package com.ai.briefing.controller;

import com.ai.briefing.dto.ApiResponse;
import com.ai.briefing.dto.UserPreferenceDTOs;
import com.ai.briefing.service.UserPreferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Preferences", description = "Manage user topic preferences and delivery schedules")
public class UserPreferenceController {

    private final UserPreferenceService userPreferenceService;

    @PostMapping("/subscribe")
    @Operation(summary = "Subscribe or update preferences",
               description = "Register a user with topics and delivery times. Re-calling updates existing preferences.")
    public ResponseEntity<ApiResponse<UserPreferenceDTOs.Response>> subscribe(
            @Valid @RequestBody UserPreferenceDTOs.Request request) {

        UserPreferenceDTOs.Response saved = userPreferenceService.createOrUpdate(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Subscription saved successfully", saved));
    }

    @GetMapping("/{email}")
    @Operation(summary = "Get preferences by email")
    public ResponseEntity<ApiResponse<UserPreferenceDTOs.Response>> getByEmail(
            @PathVariable String email) {

        return ResponseEntity.ok(ApiResponse.success(userPreferenceService.getByEmail(email)));
    }

    @GetMapping
    @Operation(summary = "Get all active subscribers")
    public ResponseEntity<ApiResponse<List<UserPreferenceDTOs.Response>>> getAllActive() {
        return ResponseEntity.ok(ApiResponse.success(userPreferenceService.getAllActive()));
    }

    @PatchMapping("/{email}/deactivate")
    @Operation(summary = "Unsubscribe a user (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable String email) {
        userPreferenceService.deactivate(email);
        return ResponseEntity.ok(ApiResponse.success("User unsubscribed", null));
    }

    @DeleteMapping("/{email}")
    @Operation(summary = "Permanently delete user preferences")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String email) {
        userPreferenceService.delete(email);
        return ResponseEntity.ok(ApiResponse.success("User deleted", null));
    }
}
