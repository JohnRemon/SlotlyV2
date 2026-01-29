package com.example.SlotlyV2.feature.event.dto;

import com.example.SlotlyV2.feature.event.RecurrenceRulesDTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RecurringEventRequest extends EventRequest {

    @NotNull(message = "Recurring rules are required")
    private RecurrenceRulesDTO recurringRulesDTO;
}
