package com.example.SlotlyV2.feature.event;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.SlotlyV2.feature.schedule.Schedule;
import com.example.SlotlyV2.feature.user.User;

public interface EventRepository extends JpaRepository<Event, Long> {

    // Find events by host
    List<Event> findByHostAndDeletedAtIsNull(User host);

    Page<Event> findByHostAndDeletedAtIsNull(User host, Pageable pageable);

    Optional<Event> findByIdAndDeletedAtIsNull(Long id);

    // Find Events by link
    Optional<Event> findByShareableIdAndDeletedAtIsNull(String shareableId);

    // Check if shareableId exists
    boolean existsByShareableId(String shareableId);

    Page<Event> findByScheduleAndDeletedAtIsNull(Schedule schedule, Pageable pageable);
}
