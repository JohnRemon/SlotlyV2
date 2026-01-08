package com.example.SlotlyV2.feature.email.event;

import com.example.SlotlyV2.feature.slot.dto.SlotCancelledEmailDTO;

import lombok.Data;

@Data
public class SlotCancelledEvent {
    private final SlotCancelledEmailDTO slotCancelledEmailDTO;
}
