package com.example.SlotlyV2.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.SlotlyV2.model.Event;
import com.example.SlotlyV2.model.User;

public interface EventRepository extends JpaRepository<Event, Long> {

    // Find events by host
    List<Event> findByHost(User host);

    // Find Events by link
    Optional<Event> findByShareableId(String shareableId);

    // Check if shareableId exists
    boolean existsByShareableId(String shareableId);

}
