package com.example.SlotlyV2.feature.booking_form.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingFormUpdateRequest {
    @NotNull(message = "Fields are required")
    private List<BookingFormFieldRequest> fields;
}
