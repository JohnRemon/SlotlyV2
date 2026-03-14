package com.example.SlotlyV2.feature.slot;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

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
import com.example.SlotlyV2.feature.schedule.DailySchedule;
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
    private final TimeZoneConverter timeZoneConverter;

    @Transactional
    public void generateSlots(Event event, Schedule schedule) {
        OffsetDateTime nowUtc = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime start = findNextScheduledStart(nowUtc, event);
        slotRepository.saveAll(slotUtils.buildSlotsByTime(schedule, event, start));
    }

    @Transactional(readOnly = true)
    public Page<SlotResponse> getSlots(Long eventId, Pageable pageable, String timeZone) {
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException("Event not found with id: " + eventId);
        }
        return slotRepository.findByEventId(eventId, pageable)
                .map(slot -> toResponse(slot, timeZone));
    }

    @Transactional(readOnly = true)
    public SlotResponse getSlotById(Long slotId, String timeZone) {
        return toResponse(slotRepository.findById(slotId)
                .orElseThrow(() -> new SlotNotFoundException("Slot not found with id: " + slotId)), timeZone);
    }

    @Transactional(readOnly = true)
    public Page<SlotResponse> getAvailableSlots(String shareableId, LocalDate date, String timeZone,
            Pageable pageable) {
        Event event = eventRepository.findByShareableIdAndDeletedAtIsNull(shareableId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        if (!event.getAvailabilityRules().getIsPublic()) {
            throw new EventNotFoundException("Event not found");
        }

        ZoneId zone = ZoneId.of(timeZone);
        OffsetDateTime start = date.atStartOfDay(zone).toOffsetDateTime();
        OffsetDateTime end = date.plusDays(1).atStartOfDay(zone).toOffsetDateTime();

        return slotRepository.findByEventAndBookingIsNullAndStartTimeBetween(
                event, start, end, pageable)
                .map(slot -> toResponse(slot, timeZone));
    }

    public Slot getRawSlotById(Long slotId) {
        return slotRepository.findById(slotId)
                .orElseThrow(() -> new SlotNotFoundException("Slot not found with id: " + slotId));
    }

    @Transactional
    public void regenerateFutureSlots(Event event) {
        Schedule schedule = event.getSchedule();
        OffsetDateTime nowUtc = OffsetDateTime.now(ZoneOffset.UTC);

        OffsetDateTime effectiveStart = findNextScheduledStart(nowUtc, event);
        deleteUnbookedSlots(event, effectiveStart);

        if (effectiveStart.isBefore(nowUtc.plusDays(event.getAvailabilityRules().getMaximumAdvanceDays()))) {
            generateSlots(event, schedule);
        }
    }

    private OffsetDateTime findNextScheduledStart(OffsetDateTime nowUtc, Event event) {
        ZoneId hostZone = ZoneId.of(event.getHost().getTimeZone());
        ZonedDateTime nowInHostZone = nowUtc.atZoneSameInstant(hostZone);
        int currentDayOfWeek = nowInHostZone.getDayOfWeek().getValue();

        DailySchedule todaySchedule = event.getSchedule().getDailySchedules().stream()
                .filter(day -> day.getDayOfWeek().equals(currentDayOfWeek) && day.isAvailable())
                .findFirst()
                .orElse(null);

        if (todaySchedule != null) {
            ZonedDateTime todayStart = nowInHostZone.toLocalDate()
                    .atTime(todaySchedule.getStartTime())
                    .atZone(hostZone); // interpret 09:00 in host's zone, not UTC
            if (todayStart.toInstant().isAfter(nowUtc.toInstant())) {
                return todayStart.toOffsetDateTime();
            }
        }

        for (int i = 1; i <= 7; i++) {
            ZonedDateTime nextDate = nowInHostZone.plusDays(i);
            int nextDayOfWeek = nextDate.getDayOfWeek().getValue();
            DailySchedule nextSchedule = event.getSchedule().getDailySchedules().stream()
                    .filter(ds -> ds.getDayOfWeek().equals(nextDayOfWeek) && ds.isAvailable())
                    .findFirst()
                    .orElse(null);
            if (nextSchedule != null) {
                return nextDate.toLocalDate()
                        .atTime(nextSchedule.getStartTime())
                        .atZone(hostZone)
                        .toOffsetDateTime();
            }
        }

        return nowUtc.plusDays(event.getAvailabilityRules().getMaximumAdvanceDays());
    }

    @Transactional
    public void deleteUnbookedSlots(Event event, OffsetDateTime effectiveStart) {
        slotRepository.deleteByEventAndStartTimeGreaterThanEqualAndBookingIsNull(event, effectiveStart);
    }

    // -- Private helpers -------------------------------------------------------

    private SlotResponse toResponse(Slot slot, String timeZone) {
        return new SlotResponse(slot, timeZoneConverter, timeZone);
    }
}
