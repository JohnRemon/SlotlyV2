package com.example.SlotlyV2.feature.booking_form.dto;

import java.util.List;

import jakarta.validation.Valid;
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
    private List<BookingFormFieldRequest> fields;
}
