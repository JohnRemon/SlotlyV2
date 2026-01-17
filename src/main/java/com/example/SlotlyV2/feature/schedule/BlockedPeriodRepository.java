package com.example.SlotlyV2.feature.schedule;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockedPeriodRepository extends JpaRepository<BlockedPeriod, UUID> {

}
