package com.example.SlotlyV2.event;

import com.example.SlotlyV2.dto.SlotCancelledEmailDTO;

import lombok.Data;

@Data
public class SlotCancelledEvent {
    private final SlotCancelledEmailDTO slotCancelledEmailDTO;
}
