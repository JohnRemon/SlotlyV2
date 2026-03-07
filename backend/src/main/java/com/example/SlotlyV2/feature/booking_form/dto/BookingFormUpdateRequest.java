package com.example.SlotlyV2.feature.booking_form.dto;

import java.util.List;

import lombok.Data;

@Data
public class BookingFormUpdateRequest {
    private List<BookingFormFieldRequest> fields;
}
