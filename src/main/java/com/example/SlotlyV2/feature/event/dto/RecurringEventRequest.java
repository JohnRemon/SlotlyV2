package com.example.SlotlyV2.feature.event.dto;

import java.time.LocalDateTime;

import com.example.SlotlyV2.feature.event.enums.RecurrenceFrequency;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecurringEventRequest {
    @NotNull(message = "Recurrence end type is required")
    private RecurrenceFrequency frequency;

    @NotNull(message = "Recurrence interval is required")
    private Integer interval;

    private Integer[] daysOfWeek;

    private Integer occurrences;

    private LocalDateTime endDate;
}
