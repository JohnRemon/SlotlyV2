package com.example.SlotlyV2.event;

import com.example.SlotlyV2.dto.BookingEmailDTO;

import lombok.Data;

@Data
public class SlotBookedEvent {
    private final BookingEmailDTO bookingEmailDTO;
}
