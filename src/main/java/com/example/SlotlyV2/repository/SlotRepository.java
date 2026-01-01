package com.example.SlotlyV2.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.SlotlyV2.model.Event;
import com.example.SlotlyV2.model.Slot;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Long> {
    List<Slot> findByEvent(Event event);

    @EntityGraph(attributePaths = { "event", "event.host" })
    List<Slot> findByEventId(Long eventId);

    @EntityGraph(attributePaths = { "event", "event.host" })
    List<Slot> findByEventAndBookedByEmailIsNullAndBookedByNameIsNull(Event event);

    Optional<Slot> findById(Long id);

    @EntityGraph(attributePaths = { "event", "event.host" })
    List<Slot> findByBookedByEmail(String email);

    Optional<Slot> findByEventIdAndStartTime(Long eventId, LocalDateTime startTime);

    Integer countByEventAndBookedByEmailIsNotNullAndBookedByNameIsNotNull(Event event);
}
