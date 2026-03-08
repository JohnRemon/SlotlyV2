package com.example.SlotlyV2.feature.schedule.dto;

import java.time.LocalTime;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyScheduleRequest {
    @NotNull(message = "day of week is required")
    @Min(1)
    @Max(7)
    private Integer dayOfWeek;

    private LocalTime startTime;
    private LocalTime endTime;

    @NotNull(message = "availability is required")
    private Boolean isAvailable;
}
