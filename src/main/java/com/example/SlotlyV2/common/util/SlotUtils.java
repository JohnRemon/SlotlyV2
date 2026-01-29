package com.example.SlotlyV2.common.util;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.SlotlyV2.feature.event.Event;
import com.example.SlotlyV2.feature.event.enums.RecurrenceFrequency;
import com.example.SlotlyV2.feature.schedule.ScheduleService;
import com.example.SlotlyV2.feature.slot.Slot;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SlotUtils {
    private final ScheduleService scheduleService;

    public List<Slot> buildSlotsByTime(Event event, OffsetDateTime start, OffsetDateTime end) {
        List<Slot> slots = new ArrayList<>();
        OffsetDateTime currentStart = start;

        Integer slotDuration = event.getAvailabilityRules().getSlotDurationMinutes();
        Integer buffer = event.getAvailabilityRules().getBufferMinutes();

        while (currentStart.plusMinutes(slotDuration).isBefore(end)
                || currentStart.plusMinutes(slotDuration).equals(end)) {

            OffsetDateTime slotEnd = currentStart.plusMinutes(slotDuration);

            if (scheduleService.isValidSlot(event.getHost(), currentStart, slotDuration)) {
                Slot slot = Slot.builder()
                        .event(event)
                        .startTime(currentStart)
                        .endTime(slotEnd)
                        .build();
                slots.add(slot);
            }
            currentStart = slotEnd.plusMinutes(buffer);
        }
        return slots;
    }

    public List<Slot> buildRecurringSlots(Event event, RecurrenceFrequency recurrenceFrequency,
            OffsetDateTime recurrenceStart, OffsetDateTime recurrenceEnd) {
        List<Slot> slots = new ArrayList<>();
        OffsetDateTime currentStart = recurrenceStart;
        Duration eventDuration = Duration.between(event.getEventStart(), event.getEventEnd());

        while (!currentStart.isAfter(recurrenceEnd)) {
            OffsetDateTime currentEnd = currentStart.plus(eventDuration);
            slots.addAll(buildSlotsByTime(event, currentStart, currentEnd));
            currentStart = getNextRecurrence(currentStart, recurrenceFrequency);
        }

        return slots;
    }

    public List<Slot> buildRecurringSlotsByOccurrences(Event event, RecurrenceFrequency recurrenceFrequency,
            Integer occurrences) {
        List<Slot> slots = new ArrayList<>();
        OffsetDateTime currentStart = event.getEventStart();
        Duration eventDuration = Duration.between(event.getEventStart(), event.getEventEnd());
        int count = 0;

        while (count < occurrences) {
            OffsetDateTime currentEnd = currentStart.plus(eventDuration);
            slots.addAll(buildSlotsByTime(event, currentStart, currentEnd));
            currentStart = getNextRecurrence(currentStart, recurrenceFrequency);
            count++;
        }

        return slots;
    }

    private OffsetDateTime getNextRecurrence(OffsetDateTime current, RecurrenceFrequency frequency) {
        return switch (frequency) {
            case DAILY -> current.plusDays(1);
            case WEEKLY -> current.plusWeeks(1);
            case MONTHLY -> current.plusMonths(1);
        };
    }
}
