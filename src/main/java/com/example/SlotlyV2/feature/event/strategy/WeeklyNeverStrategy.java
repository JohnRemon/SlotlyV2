package com.example.SlotlyV2.feature.event.strategy;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.SlotlyV2.common.util.SlotUtils;
import com.example.SlotlyV2.feature.event.Event;
import com.example.SlotlyV2.feature.event.enums.RecurrenceFrequency;
import com.example.SlotlyV2.feature.event.enums.StrategyType;
import com.example.SlotlyV2.feature.slot.Slot;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service(StrategyType.WEEKLY_NEVER)
public class WeeklyNeverStrategy implements RecurrenceStrategy {
    private final Integer MAX_YEARS = 1;
    private final SlotUtils slotUtils;

    @Override
    public List<Slot> generateSlots(final Event event) {
        return slotUtils.buildRecurringSlots(event, RecurrenceFrequency.WEEKLY, event.getEventStart(),
                event.getEventStart().plusYears(MAX_YEARS));
    }
}
