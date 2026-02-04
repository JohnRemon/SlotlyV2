package com.example.SlotlyV2.feature.booking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelBookingRequest {
    @Email(message = "Valid email is required")
    @NotBlank(message = "Attendee email is required")
    private String attendeeEmail;

    @Size(max = 1000, message = "cancellation reason must not exceed 1000 characters")
    private String cancellationReason;
}
