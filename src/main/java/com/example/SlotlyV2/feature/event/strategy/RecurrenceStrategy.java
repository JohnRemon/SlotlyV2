package com.example.SlotlyV2.feature.event.strategy;

import java.util.List;

import com.example.SlotlyV2.feature.event.Event;
import com.example.SlotlyV2.feature.slot.Slot;

public interface RecurrenceStrategy {
    List<Slot> generateSlots(Event event);
}
