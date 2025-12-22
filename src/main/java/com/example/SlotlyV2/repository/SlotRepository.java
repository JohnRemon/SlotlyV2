package com.example.SlotlyV2.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.SlotlyV2.model.Event;
import com.example.SlotlyV2.model.Slot;
import com.example.SlotlyV2.model.User;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Long> {
    List<Slot> findByEvent(Event event);

    List<Slot> findByEventId(Long eventId);

    List<Slot> findByEventAndBookedByIsNull(Event event);

    Optional<Slot> findById(Long id);

    List<Slot> findBookedBy(User user);

    List<Slot> findBookedById(Long userId);
}
