package com.example.SlotlyV2.feature.email.event;

import com.example.SlotlyV2.feature.booking.dto.BookingCancelledEmailDTO;

import lombok.Data;

@Data
public class SlotCancelledEvent {
    private final BookingCancelledEmailDTO slotCancelledEmailDTO;
}
