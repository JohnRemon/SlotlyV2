package com.example.SlotlyV2.feature.booking_form;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingFormRepository extends JpaRepository<BookingForm, UUID> {
    Optional<BookingForm> findByEventId(Long eventId);
}
