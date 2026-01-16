package com.example.SlotlyV2.feature.schedule.dto;

import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRequest {
    @NotNull(message = "days of week are required")
    private boolean[] daysOfWeek;

    @NotNull(message = "start times are required")
    private LocalTime[] startTimes;

    @NotNull(message = "end times are required")
    private LocalTime[] endTimes;
}
