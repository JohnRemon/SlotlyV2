package com.example.SlotlyV2.feature.slot;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.SlotlyV2.feature.event.Event;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Long> {
    @EntityGraph(attributePaths = { "event", "event.host" })
    List<Slot> findByEvent(Event event);

    @EntityGraph(attributePaths = { "event", "event.host" })
    List<Slot> findByEventId(Long eventId);

    @EntityGraph(attributePaths = { "event", "event.host" })
    List<Slot> findByEventAndBookedByEmailIsNullAndBookedByNameIsNull(Event event);

    @EntityGraph(attributePaths = { "event", "event.host" })
    Optional<Slot> findById(Long id);

    @EntityGraph(attributePaths = { "event", "event.host" })
    List<Slot> findByBookedByEmail(String email);

    Optional<Slot> findByEventIdAndStartTime(Long eventId, OffsetDateTime startTime);

    Integer countByEventAndBookedByEmailIsNotNullAndBookedByNameIsNotNull(Event event);

    List<Slot> findByEventAndBookedByEmailIsNullAndBookedByNameIsNullAndStartTimeAfter(Event event,
            OffsetDateTime startTime);
}
