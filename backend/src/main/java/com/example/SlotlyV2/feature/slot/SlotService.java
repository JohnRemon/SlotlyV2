package com.example.SlotlyV2.feature.slot;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.SlotlyV2.common.exception.auth.UnauthorizedAccessException;
import com.example.SlotlyV2.common.exception.event.EventNotFoundException;
import com.example.SlotlyV2.common.exception.slot.SlotNotFoundException;
import com.example.SlotlyV2.common.util.SlotUtils;
import com.example.SlotlyV2.feature.event.Event;
import com.example.SlotlyV2.feature.event.EventRepository;
import com.example.SlotlyV2.feature.event.strategy.RecurrenceStrategy;
import com.example.SlotlyV2.feature.event.strategy.RecurrenceStrategyFactory;
import com.example.SlotlyV2.feature.schedule.Schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SlotService {
    private final SlotRepository slotRepository;
    private final EventRepository eventRepository;
    private final SlotUtils slotUtils;
    private final RecurrenceStrategyFactory recurrenceStrategyFactory;

    @Transactional
    public void generateSlots(Event event, Schedule schedule) {
        slotRepository.saveAll(slotUtils.buildSlotsByTime(schedule, event, event.getEventStart(), event.getEventEnd()));
    }

    @Transactional
    public void generateSlots(Event event, Schedule schedule, OffsetDateTime start, OffsetDateTime end) {
        slotRepository.saveAll(slotUtils.buildSlotsByTime(schedule, event, start, end));
    }

    @Transactional
    public void generateSlotsRecurring(Event event, Schedule schedule) {
        String strategyType = event.getRecurrenceRules().getRecurrenceFrequency() + "_"
                + event.getRecurrenceRules().getRecurrenceEndType();
        RecurrenceStrategy recurrenceStrategy = recurrenceStrategyFactory.getStrategy(strategyType);

        slotRepository.saveAll(recurrenceStrategy.generateSlots(event, schedule));
    }

    public List<Slot> getSlots(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException("Event Not Found");
        }

        return slotRepository.findByEventId(eventId);
    }

    public List<Slot> getAvailableSlotsByShareableId(String shareableId) {
        Event event = eventRepository.findByShareableIdAndDeletedAtIsNull(shareableId)
                .orElseThrow(() -> new EventNotFoundException("Event Not Found"));

        if (!event.getAvailabilityRules().getIsPublic()) {
            throw new UnauthorizedAccessException("Event is private");
        }

        return slotRepository.findByEvent(event).stream()
                .filter(slot -> slot.isAvailable())
                .toList();
    }

    public List<Slot> getAvailableSlotsByShareableIdAndDate(String shareableId, LocalDate date, String timeZone) {
        ZoneId zone = ZoneId.of(timeZone);
        List<Slot> allAvailableSlots = getAvailableSlotsByShareableId(shareableId);
        return allAvailableSlots.stream()
                .filter(slot -> slot.getStartTime()
                        .atZoneSameInstant(zone)
                        .toLocalDate()
                        .equals(date))
                .toList();
    }

    public Slot getSlotById(Long slotId) {
        return slotRepository.findById(slotId)
                .orElseThrow(() -> new SlotNotFoundException("Slot not found with ID: " + slotId));
    }
}
