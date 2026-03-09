package com.example.SlotlyV2.feature.booking;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.SlotlyV2.feature.event.Event;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByAttendeeEmail(String attendeeEmail);

    List<Booking> findByEventAndStatus(Event event, BookingStatus bookingStatus);

    List<Booking> findByEventId(Long eventId);

    List<Booking> findByEventIdAndSlotEndTimeGreaterThanEqual(Long eventId, OffsetDateTime dateTime);

    int countByEventAndStatus(Event event, BookingStatus bookingStatus);

    @Query("SELECT b FROM Booking b JOIN FETCH b.event e JOIN FETCH e.host JOIN FETCH b.slot WHERE b.id = :id")
    Optional<Booking> findByIdWithEventAndSlot(@Param("id") Long id);
}
