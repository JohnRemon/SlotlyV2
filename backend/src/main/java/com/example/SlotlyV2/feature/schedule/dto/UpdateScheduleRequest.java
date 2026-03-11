package com.example.SlotlyV2.feature.schedule.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateScheduleRequest {
    @NotNull(message = "Days are required")
    private List<DailyScheduleRequest> days;
}
