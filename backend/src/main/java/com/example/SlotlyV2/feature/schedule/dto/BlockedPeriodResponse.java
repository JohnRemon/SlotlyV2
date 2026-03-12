package com.example.SlotlyV2.feature.schedule.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.example.SlotlyV2.feature.schedule.BlockedPeriod;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class BlockedPeriodResponse {
    @JsonProperty(index = 0)
    private UUID id;

    @JsonProperty(index = 5)
    private OffsetDateTime startTime;

    @JsonProperty(index = 10)
    private OffsetDateTime endTime;

    @JsonProperty(index = 15)
    private String reason;

    @JsonProperty(index = 20)
    private boolean isRecurring;

    public BlockedPeriodResponse(BlockedPeriod period) {
        this.id = period.getId();
        this.startTime = period.getStartTime();
        this.endTime = period.getEndTime();
        this.reason = period.getReason();
        this.isRecurring = period.getIsRecurring();
    }
}
