package com.example.SlotlyV2.feature.recurrence.dto;

import java.time.OffsetDateTime;

import com.example.SlotlyV2.feature.event.enums.RecurrenceEndType;
import com.example.SlotlyV2.feature.event.enums.RecurrenceFrequency;

import jakarta.validation.constraints.Future;
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
public class RecurrenceRulesDTO {
    @NotNull(message = "Recurrence frequency is required")
    private RecurrenceFrequency recurrenceFrequency;

    @Min(1)
    @Max(365)
    private Integer interval;

    @NotNull(message = "On which day will the event recur")
    private Integer recurrenceDayOfWeek;

    @NotNull(message = "How does the event end")
    private RecurrenceEndType recurrenceEndType;

    @Min(1)
    @Max(100)
    private Integer recurrenceOccurrences;

    @Future(message = "End date time must be in the future")
    private OffsetDateTime recurrenceEndDate;
}
