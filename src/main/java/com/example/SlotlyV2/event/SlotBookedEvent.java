package com.example.SlotlyV2.event;

import com.example.SlotlyV2.dto.BookingEmailData;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SlotBookedEvent {
    private final BookingEmailData bookingData;
}
