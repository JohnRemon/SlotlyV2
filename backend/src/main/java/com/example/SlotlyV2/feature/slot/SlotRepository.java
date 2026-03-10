package com.example.SlotlyV2.feature.slot;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.SlotlyV2.feature.event.Event;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Long> {
    @EntityGraph(attributePaths = { "event", "event.host" })
    List<Slot> findByEvent(Event event);

    @EntityGraph(attributePaths = { "event", "event.host" })
    Page<Slot> findByEventAndBookingIsNull(Event event, Pageable pageable);

    @EntityGraph(attributePaths = { "event", "event.host" })
    Page<Slot> findByEventAndBookingIsNullAndStartTimeBetween(
            Event event,
            OffsetDateTime start,
            OffsetDateTime end,
            Pageable pageable);

    @EntityGraph(attributePaths = { "event", "event.host" })
    Page<Slot> findByEventId(Long eventId, Pageable pageable);

    @EntityGraph(attributePaths = { "event", "event.host" })
    Optional<Slot> findById(Long id);

    Optional<Slot> findByEventIdAndStartTime(Long eventId, OffsetDateTime startTime);

    @Transactional
    @Modifying
    void deleteByEventIdAndEndTimeGreaterThanEqual(Long eventId, OffsetDateTime dateTime);
}
