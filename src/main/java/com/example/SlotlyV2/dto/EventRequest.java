package com.example.SlotlyV2.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
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

}
