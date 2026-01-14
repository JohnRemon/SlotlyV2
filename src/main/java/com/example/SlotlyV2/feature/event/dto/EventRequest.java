package com.example.SlotlyV2.feature.event.dto;

import java.time.LocalDateTime;

import com.example.SlotlyV2.feature.availability.AvailabilityRulesDTO;
import com.example.SlotlyV2.feature.event.RecurringRulesDTO;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequest {
    @NotBlank(message = "Event name is required")
    private String eventName;

    private String description;

    @NotNull(message = "Event start time is required")
    @Future(message = "Event must be in the future")
    private LocalDateTime eventStart;

    @NotNull(message = "Event end time is required")
    @Future(message = "Event must be in the future")
    private LocalDateTime eventEnd;

    @NotBlank(message = "Timezone is required")
    private String timeZone;

    @NotNull(message = "Rules are required")
    private AvailabilityRulesDTO rules;

    @NotNull(message = "Is the event recurring?")
    private boolean recurring;

    private RecurringRulesDTO recurringRules;
}
