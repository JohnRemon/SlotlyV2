package com.example.SlotlyV2.event;

import com.example.SlotlyV2.dto.BookingEmailData;

import lombok.Data;

@Data
public class SlotBookedEvent {
    private final BookingEmailData bookingData;
}
