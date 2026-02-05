package com.example.SlotlyV2.feature.event;

import java.time.OffsetDateTime;

import org.springframework.stereotype.Component;

import com.example.SlotlyV2.common.util.TimeZoneConverter;
import com.example.SlotlyV2.feature.availability.AvailabilityRules;
import com.example.SlotlyV2.feature.availability.dto.AvailabilityRulesDTO;
import com.example.SlotlyV2.feature.event.dto.EventRequest;
import com.example.SlotlyV2.feature.recurrence.RecurrenceRules;
import com.example.SlotlyV2.feature.recurrence.dto.RecurrenceRulesDTO;
import com.example.SlotlyV2.feature.user.UserService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EventFactory {
    private final UserService userService;
    private final TimeZoneConverter timeZoneConverter;

    public Event createFrom(EventRequest request) {
        AvailabilityRules availabilityRules = buildAvailabilityRules(
                request.getAvailabilityRulesDTO());

        OffsetDateTime utcStart = timeZoneConverter.toUtc(request.getEventStart(), request.getTimeZone());
        OffsetDateTime utcEnd = timeZoneConverter.toUtc(request.getEventEnd(), request.getTimeZone());

        Event.EventBuilder builder = Event.builder()
                .eventName(request.getEventName())
                .description(request.getDescription())
                .host(userService.getCurrentUser())
                .eventStart(utcStart)
                .eventEnd(utcEnd)
                .timeZone(request.getTimeZone())
                .availabilityRules(availabilityRules);

        if (request.getRecurrenceRulesDTO() != null) {
            RecurrenceRules recurrenceRules = buildRecurrenceRules(request.getRecurrenceRulesDTO(),
                    request.getTimeZone());

            builder.isRecurring(true)
                    .recurrenceRules(recurrenceRules);
        }

        return builder.build();
    }

    public AvailabilityRules buildAvailabilityRules(AvailabilityRulesDTO dto) {
        if (dto == null) {
            dto = new AvailabilityRulesDTO();
        }

        return AvailabilityRules.builder()
                .slotDurationMinutes(orDefault(dto.getSlotDurationMinutes(), 30))
                .maxSlotsPerUser(orDefault(dto.getMaxSlotsPerUser(), 1))
                .bufferMinutes(orDefault(dto.getBufferMinutes(), 0))
                .minimumNoticeHours(orDefault(dto.getMinimumNoticeHours(), 0))
                .maximumAdvanceDays(orDefault(dto.getMaximumAdvanceDays(), 90))
                .maxCapacity(dto.getMaxCapacity())
                .allowsCancellations(orDefault(dto.getAllowCancellations(), true))
                .isPublic(orDefault(dto.getIsPublic(), true))
                .build();
    }

    public RecurrenceRules buildRecurrenceRules(RecurrenceRulesDTO dto, String timeZone) {
        return RecurrenceRules.builder()
                .recurrenceFrequency(dto.getRecurrenceFrequency())
                .recurrenceEndType(dto.getRecurrenceEndType())
                .recurrenceDayOfWeek(dto.getRecurrenceDayOfWeek())
                .recurrenceOccurrences(dto.getRecurrenceOccurrences())
                .recurrenceEndDate(dto.getRecurrenceEndDate() != null
                        ? timeZoneConverter.toUtc(dto.getRecurrenceEndDate(), timeZone)
                        : null)
                .build();
    }

    private <T> T orDefault(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }
}
