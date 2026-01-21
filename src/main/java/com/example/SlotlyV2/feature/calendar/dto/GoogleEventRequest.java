package com.example.SlotlyV2.feature.calendar.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GoogleEventRequest {
    @NotNull(message = "Summary is required")
    private String summary;

    @NotNull(message = "Description is required")
    private String description;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endtime;

    @NotNull(message = "Timezone is required")
    private String timeZone;
}
