package com.example.SlotlyV2.common.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.SlotlyV2.feature.event.Event;
import com.example.SlotlyV2.feature.event.enums.RecurrenceFrequency;
import com.example.SlotlyV2.feature.slot.Slot;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SlotUtils {

    public List<Slot> buildSlotsByTime(Event event, LocalDateTime start, LocalDateTime end) {
        List<Slot> slots = new ArrayList<>();
        LocalDateTime currentStart = start;
        int slotDuration = event.getAvailabilityRules().getSlotDurationMinutes();

        while (!currentStart.isAfter(end.minusMinutes(slotDuration))) {
            Slot slot = Slot.builder()
                    .event(event)
                    .startTime(currentStart)
                    .endTime(currentStart.plusMinutes(slotDuration))
                    .bookedByEmail(null)
                    .bookedByName(null)
                    .bookedAt(null)
                    .build();
            slots.add(slot);
            currentStart = currentStart.plusMinutes(slotDuration);
        }

        return slots;
    }

    public List<Slot> buildRecurringSlots(Event event, RecurrenceFrequency recurrenceFrequency,
            LocalDateTime recurrenceStart, LocalDateTime recurrenceEnd) {
        List<Slot> slots = new ArrayList<>();
        LocalDateTime currentStart = recurrenceStart;
        Duration eventDuration = Duration.between(event.getEventStart(), event.getEventEnd());

        while (!currentStart.isAfter(recurrenceEnd)) {
            LocalDateTime currentEnd = currentStart.plus(eventDuration);
            slots.addAll(buildSlotsByTime(event, currentStart, currentEnd));
            currentStart = getNextRecurrence(currentStart, recurrenceFrequency);
        }

        return slots;
    }

    public List<Slot> buildRecurringSlotsByOccurrences(Event event, RecurrenceFrequency recurrenceFrequency,
            Integer occurrences) {
        List<Slot> slots = new ArrayList<>();
        LocalDateTime currentStart = event.getEventStart();
        Duration eventDuration = Duration.between(event.getEventStart(), event.getEventEnd());
        int count = 0;

        while (count < occurrences) {
            LocalDateTime currentEnd = currentStart.plus(eventDuration);
            slots.addAll(buildSlotsByTime(event, currentStart, currentEnd));
            currentStart = getNextRecurrence(currentStart, recurrenceFrequency);
            count++;
        }

        return slots;
    }

    private LocalDateTime getNextRecurrence(LocalDateTime current, RecurrenceFrequency frequency) {
        return switch (frequency) {
            case DAILY -> current.plusDays(1);
            case WEEKLY -> current.plusWeeks(1);
            case MONTHLY -> current.plusMonths(1);
        };
    }
}
