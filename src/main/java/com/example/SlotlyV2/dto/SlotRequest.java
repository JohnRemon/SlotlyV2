package com.example.SlotlyV2.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SlotRequest {
    @NotNull(message = "Event ID is required")
    private Long eventId;

    @NotNull(message = "Slot ID is required")
    private Long slotId;

    @NotNull(message = "Start time is required")
    @Future(message = "Booking must be in the future")
    private LocalDateTime slotStartTime;

    @NotBlank(message = "Attendee name is required")
    private String attendeeName;

    @Email(message = "Valid email is required")
    @NotBlank(message = "Attendee email is required")
    private String attendeeEmail;

    private String notes;
}
