package com.example.SlotlyV2.feature.event.strategy;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.SlotlyV2.common.exception.event.InvalidEventException;
import com.example.SlotlyV2.common.util.SlotUtils;
import com.example.SlotlyV2.feature.event.Event;
import com.example.SlotlyV2.feature.event.enums.RecurrenceFrequency;
import com.example.SlotlyV2.feature.event.enums.StrategyType;
import com.example.SlotlyV2.feature.slot.Slot;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service(StrategyType.DAILY_OCCURRENCES)
public class DailyOccurrencesStrategy implements RecurrenceStrategy {
    private final SlotUtils slotUtils;

    @Override
    public List<Slot> generateSlots(Event event) {
        if (event.getRecurringRules().getRecurrenceOccurrences() == null) {
            throw new InvalidEventException("Occurrences count is required");
        }
        return slotUtils.buildRecurringSlotsByOccurrences(event, RecurrenceFrequency.DAILY,
                event.getRecurringRules().getRecurrenceOccurrences());
    }
}
