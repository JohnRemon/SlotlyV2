package com.example.SlotlyV2.feature.slot;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.SlotlyV2.common.exception.event.EventNotFoundException;
import com.example.SlotlyV2.common.exception.slot.SlotNotFoundException;
import com.example.SlotlyV2.common.util.SlotUtils;
import com.example.SlotlyV2.common.util.TimeZoneConverter;
import com.example.SlotlyV2.feature.event.Event;
import com.example.SlotlyV2.feature.event.EventRepository;
import com.example.SlotlyV2.feature.event.strategy.RecurrenceStrategy;
import com.example.SlotlyV2.feature.event.strategy.RecurrenceStrategyFactory;
import com.example.SlotlyV2.feature.schedule.Schedule;
import com.example.SlotlyV2.feature.slot.dto.SlotResponse;

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
    private final TimeZoneConverter timeZoneConverter;

    @Transactional
    public void generateSlots(Event event, Schedule schedule) {
        slotRepository.saveAll(
                slotUtils.buildSlotsByTime(schedule, event, event.getEventStart(), event.getEventEnd()));
    }

    @Transactional
    public void generateSlots(Event event, Schedule schedule, OffsetDateTime start, OffsetDateTime end) {
        slotRepository.saveAll(slotUtils.buildSlotsByTime(schedule, event, start, end));
    }

    @Transactional
    public void generateSlotsRecurring(Event event, Schedule schedule) {
        String strategyType = event.getRecurrenceRules().getRecurrenceFrequency()
                + "_" + event.getRecurrenceRules().getRecurrenceEndType();
        RecurrenceStrategy strategy = recurrenceStrategyFactory.getStrategy(strategyType);
        slotRepository.saveAll(strategy.generateSlots(event, schedule));
    }

    @Transactional(readOnly = true)
    public Page<SlotResponse> getSlots(Long eventId, Pageable pageable) {
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException("Event not found with id: " + eventId);
        }
        return slotRepository.findByEventId(eventId, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public SlotResponse getSlotById(Long slotId) {
        return toResponse(slotRepository.findById(slotId)
                .orElseThrow(() -> new SlotNotFoundException("Slot not found with id: " + slotId)));
    }

    @Transactional(readOnly = true)
    public Page<SlotResponse> getAvailableSlots(String shareableId, LocalDate date, String timeZone,
            Pageable pageable) {
        Event event = eventRepository.findByShareableIdAndDeletedAtIsNull(shareableId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        if (!event.getAvailabilityRules().getIsPublic()) {
            throw new EventNotFoundException("Event not found"); // don't leak existence of private events
        }

        if (date == null || timeZone == null) {
            return slotRepository.findByEventAndBookingIsNull(event, pageable)
                    .map(this::toResponse);
        }

        ZoneId zone = ZoneId.of(timeZone);
        OffsetDateTime start = date.atStartOfDay(zone).toOffsetDateTime();
        OffsetDateTime end = date.plusDays(1).atStartOfDay(zone).toOffsetDateTime();

        return slotRepository.findByEventAndBookingIsNullAndStartTimeBetween(
                event, start, end, pageable)
                .map(this::toResponse);
    }

    // Called internally — returns raw Slot for booking logic
    public Slot getRawSlotById(Long slotId) {
        return slotRepository.findById(slotId)
                .orElseThrow(() -> new SlotNotFoundException("Slot not found with id: " + slotId));
    }

    public void regenerateFutureSlots(Event event) {
        OffsetDateTime nowUtc = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime effectiveStart = nowUtc.isAfter(event.getEventStart())
                ? nowUtc
                : event.getEventStart();

        deleteUnbookedSlots(event, effectiveStart);

        if (effectiveStart.isBefore(event.getEventEnd())) {
            generateSlots(event, event.getSchedule(), effectiveStart, event.getEventEnd());
        }
    }

    public void deleteUnbookedSlots(Event event, OffsetDateTime effectiveStart) {
        List<Slot> unbookedSlots = slotRepository.findByEvent(event)
                .stream()
                .filter(slot -> slot.getStartTime().isAfter(effectiveStart))
                .filter(slot -> slot.getBooking() == null || !slot.getBooking().isActive())
                .toList();

        slotRepository.deleteAll(unbookedSlots);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private SlotResponse toResponse(Slot slot) {
        return new SlotResponse(slot, timeZoneConverter);
    }
}
