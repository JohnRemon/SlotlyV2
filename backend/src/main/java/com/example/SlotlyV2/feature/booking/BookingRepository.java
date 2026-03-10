package com.example.SlotlyV2.feature.booking;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.SlotlyV2.feature.event.Event;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByAttendeeEmail(String attendeeEmail);

    Page<Booking> findByAttendeeEmail(String attendeeEmail, Pageable pageable);

    List<Booking> findByEventAndStatus(Event event, BookingStatus bookingStatus);

    Page<Booking> findByEventAndStatus(Event event, BookingStatus bookingStatus, Pageable pageable);

    List<Booking> findByEventId(Long eventId);

    Page<Booking> findByEventId(Long eventId, Pageable pageable);

    List<Booking> findByEventIdAndSlotEndTimeGreaterThanEqual(Long eventId, OffsetDateTime dateTime);

    Page<Booking> findByEventIdAndSlotEndTimeGreaterThanEqual(Long eventId, OffsetDateTime dateTime, Pageable pageable);

    int countByEventAndStatus(Event event, BookingStatus bookingStatus);

    @Query("SELECT b FROM Booking b JOIN FETCH b.event e JOIN FETCH e.host JOIN FETCH b.slot WHERE b.id = :id")
    Optional<Booking> findByIdWithEventAndSlot(@Param("id") Long id);
}
