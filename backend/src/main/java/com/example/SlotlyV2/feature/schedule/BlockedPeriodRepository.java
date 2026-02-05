package com.example.SlotlyV2.feature.schedule;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockedPeriodRepository extends JpaRepository<BlockedPeriod, UUID> {
    List<BlockedPeriod> findByUserId(Long id);

    List<BlockedPeriod> findByUserIdAndEndTimeAfterAndStartTimeBefore(
            Long userId, OffsetDateTime newStart, OffsetDateTime newEnd);
}
