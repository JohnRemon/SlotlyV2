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
@Service(StrategyType.MONTHLY_DATE)
public class MonthlyDateStrategy implements RecurrenceStrategy {
    private final SlotUtils slotUtils;

    @Override
    public List<Slot> generateSlots(Event event) {
        if (event.getRecurringRules().getRecurrenceEndDate() == null) {
            throw new InvalidEventException("End date of recurrence is required");
        }
        return slotUtils.buildRecurringSlots(event, RecurrenceFrequency.MONTHLY, event.getEventStart(),
                event.getRecurringRules().getRecurrenceEndDate());
    }
}
