package com.example.SlotlyV2.feature.schedule;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.SlotlyV2.feature.user.User;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {
    Optional<Schedule> findByUser(User user);

    List<Schedule> findAllByUser(User user);

    Optional<Schedule> findByUserAndIsDefaultTrue(User user);
}
