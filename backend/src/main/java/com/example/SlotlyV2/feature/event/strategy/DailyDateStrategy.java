package com.example.SlotlyV2.feature.event.strategy;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.SlotlyV2.common.util.SlotUtils;
import com.example.SlotlyV2.feature.event.Event;
import com.example.SlotlyV2.feature.event.enums.StrategyType;
import com.example.SlotlyV2.feature.schedule.Schedule;
import com.example.SlotlyV2.feature.slot.Slot;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service(StrategyType.DAILY_DATE)
public class DailyDateStrategy implements RecurrenceStrategy {
    private final SlotUtils slotUtils;

    @Override
    public List<Slot> generateSlots(Event event, Schedule schedule) {
        return slotUtils.buildRecurringSlots(event, schedule);
    }
}
