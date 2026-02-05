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
public interface BookingGoogleEventRepository extends JpaRepository<BookingGoogleEvent, UUID> {
    Optional<BookingGoogleEvent> findByBookingId(Long id);

    Optional<BookingGoogleEvent> findByGoogleEventId(String id);

    List<BookingGoogleEvent> findBySyncStatus(SyncStatus status);

    @Transactional
    @Modifying
    void deleteByBookingId(Long id);
}
