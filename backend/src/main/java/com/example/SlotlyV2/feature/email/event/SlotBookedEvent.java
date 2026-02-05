package com.example.SlotlyV2.feature.email.event;

import com.example.SlotlyV2.feature.email.dto.BookingEmailDTO;

import lombok.Data;

@Data
public class SlotBookedEvent {
    private final BookingEmailDTO bookingEmailDTO;
}
