package com.example.SlotlyV2.feature.schedule.dto;

import java.time.OffsetDateTime;

import com.example.SlotlyV2.feature.schedule.BlockedPeriod;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BlockedPeriodResponse {
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private String reason;
    private boolean isRecurring;

    public BlockedPeriodResponse(BlockedPeriod period) {
        this.startTime = period.getStartTime();
        this.endTime = period.getEndTime();
        this.reason = period.getReason();
        this.isRecurring = period.isRecurring();
    }
}
