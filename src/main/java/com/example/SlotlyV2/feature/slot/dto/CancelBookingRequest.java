package com.example.SlotlyV2.feature.slot.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CancelBookingRequest {
    @NotNull(message = "Event ID is required")
    private Long eventId;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @Email(message = "Valid email is required")
    @NotBlank(message = "Attendee email is required")
    private String attendeeEmail;
}
