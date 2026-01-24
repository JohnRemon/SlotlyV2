package com.example.SlotlyV2.feature.calendar;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.example.SlotlyV2.feature.calendar.enums.SyncStatus;

import jakarta.transaction.Transactional;

@Repository
public interface SlotGoogleEventRepository extends JpaRepository<SlotGoogleEvent, UUID> {
    Optional<SlotGoogleEvent> findBySlotId(Long id);

    Optional<SlotGoogleEvent> findByGoogleEventId(String id);

    List<SlotGoogleEvent> findBySyncStatus(SyncStatus status);

    @Transactional
    @Modifying
    void deleteBySlotId(Long id);
}
