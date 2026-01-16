package com.example.SlotlyV2.feature.event;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.SlotlyV2.common.exception.auth.UnauthorizedAccessException;
import com.example.SlotlyV2.common.exception.event.EventNotFoundException;
import com.example.SlotlyV2.common.exception.event.InvalidEventException;
import com.example.SlotlyV2.feature.availability.AvailabilityRules;
import com.example.SlotlyV2.feature.email.dto.EventCancelledEmailDTO;
import com.example.SlotlyV2.feature.email.event.EventCancelledEvent;
import com.example.SlotlyV2.feature.event.dto.EventRequest;
import com.example.SlotlyV2.feature.event.dto.EventResponse;
import com.example.SlotlyV2.feature.event.dto.RecurringEventRequest;
import com.example.SlotlyV2.feature.event.enums.RecurrenceFrequency;
import com.example.SlotlyV2.feature.slot.SlotService;
import com.example.SlotlyV2.feature.user.User;
import com.example.SlotlyV2.feature.user.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
    private final EventRepository eventRepository;
    private final SlotService slotService;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(rollbackOn = Exception.class)
    public Event createEvent(EventRequest request) {
        // Get current user
        User host = userService.getCurrentUser();

        // Verify Start and End Dates
        if (!request.getEventEnd().isAfter(request.getEventStart())) {
            throw new InvalidEventException("Event end must be after start");
        }

        ZoneId zone = ZoneId.of(request.getTimeZone());
        ZonedDateTime now = ZonedDateTime.now(zone);

        if (request.getEventStart().atZone(zone).isBefore(now)) {
            throw new InvalidEventException("Event must start in the future");
        }

        // Creat the Event
        AvailabilityRules availabilityRules = AvailabilityRules.builder()
                .slotDurationMinutes(request.getAvailabilityRulesDTO().getSlotDurationMinutes())
                .maxSlotsPerUser(request.getAvailabilityRulesDTO().getMaxSlotsPerUser())
                .allowsCancellations(request.getAvailabilityRulesDTO().isAllowCancellations())
                .isPublic(request.getAvailabilityRulesDTO().isPublic())
                .maxCapacity(request.getAvailabilityRulesDTO().getMaxCapacity())
                .build();

        Event event = Event.builder()
                .eventName(request.getEventName())
                .description(request.getDescription())
                .host(host)
                .eventStart(request.getEventStart())
                .eventEnd(request.getEventEnd())
                .timeZone(request.getTimeZone())
                .availabilityRules(availabilityRules)
                .build();

        Event savedEvent = eventRepository.save(event);

        slotService.generateSlots(savedEvent);

        return savedEvent;
    }

    @Transactional(rollbackOn = Exception.class)
    public Event createRecurringEvent(RecurringEventRequest request) {
        // Validate the Event
        validateRecurringEvent(request);

        // Create the event
        Event event = buildRecurringEvent(request);

        // Save the event
        event = eventRepository.save(event);

        // Generate slots
        slotService.generateSlotsRecurring(event);

        return event;
    }

    public Page<EventResponse> getEvents(User host, Pageable pageable) {
        return eventRepository.findByHost(host, pageable)
                .map(EventResponse::new);
    }

    public Event getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event Not Found with Id " + id));

        if (!event.getHost().getId().equals(userService.getCurrentUser().getId())) {
            throw new UnauthorizedAccessException("You are not authorized to access other user's event");
        }

        return event;
    }

    @Transactional(rollbackOn = Exception.class)
    public void deleteEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event Not Found"));

        if (!event.getHost().getId().equals(userService.getCurrentUser().getId())) {
            throw new UnauthorizedAccessException("You are not authorized to delete other user's event");
        }

        EventCancelledEmailDTO data = new EventCancelledEmailDTO(
                event.getId(),
                event.getEventName(),
                event.getSlots().stream()
                        .map(slot -> slot.getBookedByEmail())
                        .toList());

        eventRepository.delete(event);
        eventPublisher.publishEvent(new EventCancelledEvent(data));
    }

    public Event getEventByShareableId(String shareableId) {
        Event event = eventRepository.findByShareableId(shareableId)
                .orElseThrow(() -> new EventNotFoundException("Event Not Found"));

        if (!event.getAvailabilityRules().isPublic()) {
            throw new UnauthorizedAccessException("You are not authorized to access this event");
        }

        return event;
    }

    private Event buildRecurringEvent(RecurringEventRequest request) {
        AvailabilityRules availabilityRules = AvailabilityRules.builder()
                .slotDurationMinutes(request.getAvailabilityRulesDTO().getSlotDurationMinutes())
                .maxSlotsPerUser(request.getAvailabilityRulesDTO().getMaxSlotsPerUser())
                .allowsCancellations(request.getAvailabilityRulesDTO().isAllowCancellations())
                .isPublic(request.getAvailabilityRulesDTO().isPublic())
                .maxCapacity(request.getAvailabilityRulesDTO().getMaxCapacity())
                .build();

        RecurrenceRules recurringRules = RecurrenceRules.builder()
                .recurrenceFrequency(request.getRecurringRulesDTO().getRecurrenceFrequency())
                .recurrenceEndType(request.getRecurringRulesDTO().getRecurrenceEndType())
                .recurrenceDayOfWeek(request.getRecurringRulesDTO().getRecurrenceDayOfWeek())
                .recurrenceOccurrences(request.getRecurringRulesDTO().getRecurrenceOccurrences())
                .recurrenceEndDate(request.getRecurringRulesDTO().getRecurrenceEndDate())
                .build();

        return Event.builder()
                .eventName(request.getEventName())
                .description(request.getDescription())
                .host(userService.getCurrentUser())
                .eventStart(request.getEventStart())
                .eventEnd(request.getEventEnd())
                .timeZone(request.getTimeZone())
                .isRecurring(true)
                .availabilityRules(availabilityRules)
                .recurringRules(recurringRules)
                .build();
    }

    private void validateRecurringEvent(RecurringEventRequest request) {
        if (!request.getEventEnd().isAfter(request.getEventStart())) {
            throw new InvalidEventException("Event end must be after start");
        }

        if (request.getRecurringRulesDTO().getRecurrenceEndDate() != null
                && !request.getRecurringRulesDTO().getRecurrenceEndDate().isAfter(request.getEventStart())) {
            throw new InvalidEventException("Recurrence end date must be after event start");
        }

        if (request.getRecurringRulesDTO().getRecurrenceFrequency() == RecurrenceFrequency.WEEKLY
                && request.getRecurringRulesDTO().getRecurrenceDayOfWeek() == null) {
            throw new InvalidEventException("Day of week is required for weekly recurrence");
        }

        ZoneId zone = ZoneId.of(request.getTimeZone());
        ZonedDateTime now = ZonedDateTime.now(zone);

        if (request.getEventStart().atZone(zone).isBefore(now)) {
            throw new InvalidEventException("Event must start in the future");
        }
    }
}
