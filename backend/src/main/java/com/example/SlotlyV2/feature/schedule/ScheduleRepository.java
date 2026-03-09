package com.example.SlotlyV2.feature.schedule;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.SlotlyV2.feature.user.User;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {
    Optional<Schedule> findByUser(User user);

    Page<Schedule> findAllByUser(User user, Pageable pageable);

    Optional<Schedule> findByUserAndIsDefaultTrue(User user);

    int countByUser(User user);
}
