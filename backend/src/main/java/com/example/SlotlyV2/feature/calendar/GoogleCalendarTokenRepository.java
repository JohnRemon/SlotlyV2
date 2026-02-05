package com.example.SlotlyV2.feature.calendar;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository
public interface GoogleCalendarTokenRepository extends JpaRepository<GoogleCalendarToken, UUID> {
    Optional<GoogleCalendarToken> findByUserId(Long id);

    @Transactional
    @Modifying
    void deleteByUserId(Long id);

    boolean existsByUserId(Long id);
}
