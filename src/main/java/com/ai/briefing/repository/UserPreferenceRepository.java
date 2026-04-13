package com.ai.briefing.repository;

import com.ai.briefing.model.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {
    Optional<UserPreference> findByEmail(String email);
    List<UserPreference> findByActiveTrue();
    boolean existsByEmail(String email);
}
