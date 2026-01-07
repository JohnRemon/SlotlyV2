package com.example.SlotlyV2.event;

import com.example.SlotlyV2.dto.EventCancelledEmailDTO;

import lombok.Data;

@Data
public class EventCancelledEvent {
    private final EventCancelledEmailDTO eventCancelledEmailDTO;
}
