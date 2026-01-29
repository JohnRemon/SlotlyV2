package com.example.SlotlyV2.feature.slot.dto;

import java.time.OffsetDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlotRequest {
    @NotNull(message = "Event ID is required")
    private Long eventId;

    @NotNull(message = "Start time is required")
    @Future(message = "Booking must be in the future")
    private OffsetDateTime startTime;

    @NotBlank(message = "Attendee name is required")
    private String attendeeName;

    @Email(message = "Valid email is required")
    @NotBlank(message = "Attendee email is required")
    private String attendeeEmail;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
}
