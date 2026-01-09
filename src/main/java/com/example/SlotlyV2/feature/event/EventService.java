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
        Event event = new Event();
        event.setEventName(request.getEventName());
        event.setDescription(request.getDescription());
        event.setHost(host);
        event.setEventStart(request.getEventStart());
        event.setEventEnd(request.getEventEnd());
        event.setTimeZone(request.getTimeZone());

        AvailabilityRules rules = new AvailabilityRules();
        rules.setSlotDurationMinutes(request.getRules().getSlotDurationMinutes());
        rules.setMaxSlotsPerUser(request.getRules().getMaxSlotsPerUser());
        rules.setAllowsCancellations(request.getRules().getAllowsCancellations());
        rules.setIsPublic(request.getRules().getIsPublic());

        event.setRules(rules);

        Event savedEvent = eventRepository.save(event);

        slotService.generateSlots(savedEvent);

        return savedEvent;
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

        if (!event.getRules().getIsPublic()) {
            throw new UnauthorizedAccessException("You are not authorized to access this event");
        }

        return event;
    }

}
