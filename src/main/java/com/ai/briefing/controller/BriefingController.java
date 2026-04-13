package com.ai.briefing.controller;

import com.ai.briefing.dto.ApiResponse;
import com.ai.briefing.dto.BriefingRequestDTO;
import com.ai.briefing.dto.BriefingResponseDTO;
import com.ai.briefing.model.BriefingCategory;
import com.ai.briefing.model.BriefingStatus;
import com.ai.briefing.service.BriefingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/briefings")
@RequiredArgsConstructor
@Tag(name = "Briefings", description = "CRUD operations for AI Briefings")
public class BriefingController {

    private final BriefingService briefingService;

    // ── POST /api/v1/briefings ────────────────────────────────────────────────

    @PostMapping
    @Operation(summary = "Create a new briefing")
    public ResponseEntity<ApiResponse<BriefingResponseDTO>> create(
            @Valid @RequestBody BriefingRequestDTO request) {

        BriefingResponseDTO dto = briefingService.createBriefing(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Briefing created successfully", dto));
    }

    // ── GET /api/v1/briefings ─────────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "Get all briefings")
    public ResponseEntity<ApiResponse<List<BriefingResponseDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(briefingService.getAllBriefings()));
    }

    // ── GET /api/v1/briefings/{id} ────────────────────────────────────────────

    @GetMapping("/{id}")
    @Operation(summary = "Get a briefing by ID")
    public ResponseEntity<ApiResponse<BriefingResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(briefingService.getBriefingById(id)));
    }

    // ── GET /api/v1/briefings/status/{status} ─────────────────────────────────

    @GetMapping("/status/{status}")
    @Operation(summary = "Get briefings by status")
    public ResponseEntity<ApiResponse<List<BriefingResponseDTO>>> getByStatus(
            @PathVariable BriefingStatus status) {

        return ResponseEntity.ok(ApiResponse.success(briefingService.getBriefingsByStatus(status)));
    }

    // ── GET /api/v1/briefings/category/{category} ─────────────────────────────

    @GetMapping("/category/{category}")
    @Operation(summary = "Get briefings by category")
    public ResponseEntity<ApiResponse<List<BriefingResponseDTO>>> getByCategory(
            @PathVariable BriefingCategory category) {

        return ResponseEntity.ok(ApiResponse.success(briefingService.getBriefingsByCategory(category)));
    }

    // ── GET /api/v1/briefings/search?keyword= ─────────────────────────────────

    @GetMapping("/search")
    @Operation(summary = "Search briefings by keyword")
    public ResponseEntity<ApiResponse<List<BriefingResponseDTO>>> search(
            @RequestParam String keyword) {

        return ResponseEntity.ok(ApiResponse.success(briefingService.searchBriefings(keyword)));
    }

    // ── PUT /api/v1/briefings/{id} ────────────────────────────────────────────

    @PutMapping("/{id}")
    @Operation(summary = "Update a briefing")
    public ResponseEntity<ApiResponse<BriefingResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody BriefingRequestDTO request) {

        return ResponseEntity.ok(
                ApiResponse.success("Briefing updated successfully", briefingService.updateBriefing(id, request)));
    }

    // ── PATCH /api/v1/briefings/{id}/publish ──────────────────────────────────

    @PatchMapping("/{id}/publish")
    @Operation(summary = "Publish a briefing")
    public ResponseEntity<ApiResponse<BriefingResponseDTO>> publish(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Briefing published", briefingService.publishBriefing(id)));
    }

    // ── PATCH /api/v1/briefings/{id}/archive ──────────────────────────────────

    @PatchMapping("/{id}/archive")
    @Operation(summary = "Archive a briefing")
    public ResponseEntity<ApiResponse<BriefingResponseDTO>> archive(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Briefing archived", briefingService.archiveBriefing(id)));
    }

    // ── DELETE /api/v1/briefings/{id} ─────────────────────────────────────────

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a briefing")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        briefingService.deleteBriefing(id);
        return ResponseEntity.ok(ApiResponse.success("Briefing deleted successfully", null));
    }
}
