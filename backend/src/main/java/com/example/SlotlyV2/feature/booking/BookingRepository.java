package com.example.SlotlyV2.feature.booking;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.SlotlyV2.feature.event.Event;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByAttendeeEmail(String attendeeEmail);

    List<Booking> findByEventAndStatus(Event event, BookingStatus bookingStatus);

    Integer countByEventAndStatus(Event event, BookingStatus bookingStatus);
}
