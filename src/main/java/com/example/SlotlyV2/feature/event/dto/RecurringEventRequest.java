package com.example.SlotlyV2.feature.event.dto;

import com.example.SlotlyV2.feature.event.RecurringRulesDTO;

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

    @NotNull(message = "Recurring rules are required")
    private RecurringRulesDTO recurringRulesDTO;
}
