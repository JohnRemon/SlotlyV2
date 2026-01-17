package com.example.SlotlyV2.feature.schedule.dto;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockedPeriodRequest {
    @Future(message = "start time must be in the future")
    @NotNull(message = "start time is required")
    private OffsetDateTime startTime;

    @Future(message = "end time must be in the future")
    @NotNull(message = "end time is required")
    private OffsetDateTime endTime;

    private String reason;

    @NotNull(message = "recurrence is required")
    private boolean isRecurring;
}
