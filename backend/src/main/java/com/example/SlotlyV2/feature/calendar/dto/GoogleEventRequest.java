package com.example.SlotlyV2.feature.calendar.dto;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GoogleEventRequest {
    @NotNull(message = "Summary is required")
    private String summary;

    @NotNull(message = "Description is required")
    private String description;

    @NotNull(message = "Start time is required")
    private OffsetDateTime startTime;

    @NotNull(message = "End time is required")
    private OffsetDateTime endTime;
}
