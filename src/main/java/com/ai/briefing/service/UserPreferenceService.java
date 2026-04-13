package com.ai.briefing.service;

import com.ai.briefing.dto.UserPreferenceDTOs;
import com.ai.briefing.model.UserPreference;
import com.ai.briefing.repository.UserPreferenceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserPreferenceService {

    private final UserPreferenceRepository repo;

    public UserPreferenceDTOs.Response createOrUpdate(UserPreferenceDTOs.Request req) {
        UserPreference pref = repo.findByEmail(req.getEmail())
                .orElse(UserPreference.builder().email(req.getEmail()).build());

        pref.setName(req.getName());
        pref.setPersona(req.getPersona());
        pref.setActive(true);

        if (req.getTopics() != null && !req.getTopics().isEmpty()) {
            pref.setTopics(String.join(",", req.getTopics()));
        }
        if (req.getDeliveryTimes() != null && !req.getDeliveryTimes().isEmpty()) {
            pref.setDeliveryTimes(String.join(",", req.getDeliveryTimes()));
        }

        return toResponse(repo.save(pref));
    }

    @Transactional(readOnly = true)
    public UserPreferenceDTOs.Response getByEmail(String email) {
        return toResponse(findOrThrow(email));
    }

    @Transactional(readOnly = true)
    public List<UserPreferenceDTOs.Response> getAllActive() {
        return repo.findByActiveTrue().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public void deactivate(String email) {
        UserPreference pref = findOrThrow(email);
        pref.setActive(false);
        repo.save(pref);
        log.info("Deactivated user: {}", email);
    }

    public void delete(String email) {
        UserPreference pref = findOrThrow(email);
        repo.delete(pref);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private UserPreference findOrThrow(String email) {
        return repo.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));
    }

    private UserPreferenceDTOs.Response toResponse(UserPreference p) {
        UserPreferenceDTOs.Response r = new UserPreferenceDTOs.Response();
        r.setId(p.getId());
        r.setName(p.getName());
        r.setEmail(p.getEmail());
        r.setTopics(p.getTopicList());
        r.setDeliveryTimes(p.getDeliveryTimeList());
        r.setPersona(p.getPersona());
        r.setActive(p.isActive());
        r.setCreatedAt(p.getCreatedAt());
        return r;
    }
}
