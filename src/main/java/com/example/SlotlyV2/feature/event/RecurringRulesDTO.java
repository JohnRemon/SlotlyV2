package com.example.SlotlyV2.feature.event;

import java.time.LocalDateTime;

import com.example.SlotlyV2.feature.event.enums.RecurrenceFrequency;
import com.example.SlotlyV2.feature.event.enums.RecurringEndType;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecurringRulesDTO {
    @NotNull(message = "Recurrence frequency is required")
    private RecurrenceFrequency recurrenceFrequency;

    @NotNull(message = "On which day will the event recur")
    private Integer recurrenceDayOfWeek;

    @NotNull(message = "How does the event end")
    private RecurringEndType recurringEndType;

    private Integer recurrenceOccurrences;

    private LocalDateTime recurrenceEndDate;
}
