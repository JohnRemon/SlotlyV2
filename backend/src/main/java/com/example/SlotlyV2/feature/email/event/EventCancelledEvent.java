package com.example.SlotlyV2.feature.email.event;

import com.example.SlotlyV2.feature.email.dto.EventCancelledEmailDTO;

import lombok.Data;

@Data
public class EventCancelledEvent {
    private final EventCancelledEmailDTO eventCancelledEmailDTO;
}
