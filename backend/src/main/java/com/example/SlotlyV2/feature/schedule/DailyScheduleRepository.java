package com.example.SlotlyV2.feature.schedule;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyScheduleRepository extends JpaRepository<DailySchedule, UUID> {
    List<DailySchedule> findByUserId(Long id);

    Optional<DailySchedule> findByUserIdAndDayOfWeek(Long id, Integer dayOfWeek);

    List<DailySchedule> findByUserIdOrderByDayOfWeek(Long id);
}
