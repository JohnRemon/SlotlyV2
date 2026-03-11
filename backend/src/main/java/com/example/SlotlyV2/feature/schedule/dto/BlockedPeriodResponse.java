package com.example.SlotlyV2.feature.schedule.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.example.SlotlyV2.feature.schedule.BlockedPeriod;

import lombok.Value;

@Value
public class BlockedPeriodResponse {
    private UUID id;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private String reason;
    private boolean isRecurring;

    public BlockedPeriodResponse(BlockedPeriod period) {
        this.id = period.getId();
        this.startTime = period.getStartTime();
        this.endTime = period.getEndTime();
        this.reason = period.getReason();
        this.isRecurring = period.getIsRecurring();
    }
}
