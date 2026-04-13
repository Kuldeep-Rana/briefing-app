package com.ai.briefing.service;

import com.ai.briefing.dto.BriefingItemRequestDTO;
import com.ai.briefing.dto.BriefingItemResponseDTO;
import com.ai.briefing.dto.BriefingRequestDTO;
import com.ai.briefing.dto.BriefingResponseDTO;
import com.ai.briefing.model.Briefing;
import com.ai.briefing.model.BriefingCategory;
import com.ai.briefing.model.BriefingItem;
import com.ai.briefing.model.BriefingStatus;
import com.ai.briefing.repository.BriefingRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BriefingServiceImpl implements BriefingService {

    private final BriefingRepository briefingRepository;

    // ── Create ────────────────────────────────────────────────────────────────

    @Override
    public BriefingResponseDTO createBriefing(BriefingRequestDTO request) {
        log.info("Creating briefing with title: {}", request.getTitle());

        Briefing briefing = Briefing.builder()
                .title(request.getTitle())
                .summary(request.getSummary())
                .category(request.getCategory())
                .scheduledAt(request.getScheduledAt())
                .createdBy(request.getCreatedBy())
                .status(request.getScheduledAt() != null ? BriefingStatus.SCHEDULED : BriefingStatus.DRAFT)
                .build();

        if (request.getItems() != null) {
            List<BriefingItem> items = mapToItems(request.getItems(), briefing);
            briefing.setItems(items);
        }

        Briefing saved = briefingRepository.save(briefing);
        log.info("Briefing created with id: {}", saved.getId());
        return toResponseDTO(saved);
    }

    // ── Read ─────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public BriefingResponseDTO getBriefingById(Long id) {
        return toResponseDTO(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BriefingResponseDTO> getAllBriefings() {
        return briefingRepository.findAll().stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BriefingResponseDTO> getBriefingsByStatus(BriefingStatus status) {
        return briefingRepository.findByStatusOrderByCreatedAtDesc(status)
                .stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BriefingResponseDTO> getBriefingsByCategory(BriefingCategory category) {
        return briefingRepository.findByCategoryOrderByCreatedAtDesc(category)
                .stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BriefingResponseDTO> searchBriefings(String keyword) {
        return briefingRepository.searchByKeyword(keyword)
                .stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @Override
    public BriefingResponseDTO updateBriefing(Long id, BriefingRequestDTO request) {
        log.info("Updating briefing id: {}", id);
        Briefing briefing = findOrThrow(id);

        briefing.setTitle(request.getTitle());
        briefing.setSummary(request.getSummary());
        briefing.setCategory(request.getCategory());
        briefing.setScheduledAt(request.getScheduledAt());

        if (request.getItems() != null) {
            briefing.getItems().clear();
            briefing.getItems().addAll(mapToItems(request.getItems(), briefing));
        }

        return toResponseDTO(briefingRepository.save(briefing));
    }

    @Override
    public BriefingResponseDTO publishBriefing(Long id) {
        log.info("Publishing briefing id: {}", id);
        Briefing briefing = findOrThrow(id);
        briefing.setStatus(BriefingStatus.PUBLISHED);
        return toResponseDTO(briefingRepository.save(briefing));
    }

    @Override
    public BriefingResponseDTO archiveBriefing(Long id) {
        log.info("Archiving briefing id: {}", id);
        Briefing briefing = findOrThrow(id);
        briefing.setStatus(BriefingStatus.ARCHIVED);
        return toResponseDTO(briefingRepository.save(briefing));
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @Override
    public void deleteBriefing(Long id) {
        log.info("Deleting briefing id: {}", id);
        findOrThrow(id);
        briefingRepository.deleteById(id);
    }

    // ── Scheduler hook ────────────────────────────────────────────────────────

    @Override
    public void publishScheduledBriefings() {
        List<Briefing> due = briefingRepository.findDueForPublishing(LocalDateTime.now());
        log.info("Publishing {} scheduled briefing(s)", due.size());
        due.forEach(b -> {
            b.setStatus(BriefingStatus.PUBLISHED);
            briefingRepository.save(b);
            log.info("Auto-published briefing id: {}", b.getId());
        });
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Briefing findOrThrow(Long id) {
        return briefingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Briefing not found with id: " + id));
    }

    private List<BriefingItem> mapToItems(List<BriefingItemRequestDTO> dtos, Briefing briefing) {
        return dtos.stream().map(dto -> BriefingItem.builder()
                .briefing(briefing)
                .headline(dto.getHeadline())
                .content(dto.getContent())
                .source(dto.getSource())
                .sourceUrl(dto.getSourceUrl())
                .priority(dto.getPriority() != null ? dto.getPriority() : 0)
                .build()).collect(Collectors.toList());
    }

    private BriefingResponseDTO toResponseDTO(Briefing b) {
        List<BriefingItemResponseDTO> itemDTOs = b.getItems() == null
                ? Collections.emptyList()
                : b.getItems().stream().map(i -> BriefingItemResponseDTO.builder()
                        .id(i.getId())
                        .headline(i.getHeadline())
                        .content(i.getContent())
                        .source(i.getSource())
                        .sourceUrl(i.getSourceUrl())
                        .priority(i.getPriority())
                        .createdAt(i.getCreatedAt())
                        .build()).collect(Collectors.toList());

        return BriefingResponseDTO.builder()
                .id(b.getId())
                .title(b.getTitle())
                .summary(b.getSummary())
                .status(b.getStatus())
                .category(b.getCategory())
                .createdAt(b.getCreatedAt())
                .updatedAt(b.getUpdatedAt())
                .scheduledAt(b.getScheduledAt())
                .createdBy(b.getCreatedBy())
                .items(itemDTOs)
                .itemCount(itemDTOs.size())
                .build();
    }
}
