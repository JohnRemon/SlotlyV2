package com.example.SlotlyV2.feature.booking_form.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingFormRequest {
    @Valid
    @NotEmpty(message = "booking form fields are required")
    private List<BookingFormFieldRequest> fields;
}
