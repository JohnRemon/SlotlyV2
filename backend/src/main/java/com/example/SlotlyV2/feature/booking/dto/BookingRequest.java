package com.example.SlotlyV2.feature.booking.dto;

import com.example.SlotlyV2.feature.booking_form.dto.BookingFormSubmissionRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
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
public class BookingRequest {
    @NotNull(message = "Slot Id is required")
    private Long slotId;

    @NotBlank(message = "Attendee name is required")
    private String attendeeName;

    @Email(message = "Valid email is required")
    @NotBlank(message = "Attendee email is required")
    private String attendeeEmail;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    @Valid
    private BookingFormSubmissionRequest formSubmission;
}
