package com.example.SlotlyV2.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.SlotlyV2.dto.EventRequest;
import com.example.SlotlyV2.exception.EventNotFoundException;
import com.example.SlotlyV2.exception.InvalidEventException;
import com.example.SlotlyV2.exception.UnauthorizedAccessException;
import com.example.SlotlyV2.model.Event;
import com.example.SlotlyV2.repository.EventRepository;
import com.example.SlotlyV2.model.*;

import jakarta.transaction.Transactional;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final SlotService slotService;
    private final UserService userService;

    public EventService(EventRepository eventRepository, SlotService slotService, UserService userService) {
        this.eventRepository = eventRepository;
        this.slotService = slotService;
        this.userService = userService;
    }

    @Transactional
    public Event createEvent(EventRequest request) {
        // Get current user
        User host = userService.getCurrentUser();

        // Verify Start and End Dates
        if (request.getEventEnd().isBefore(request.getEventStart())) {
            throw new InvalidEventException("Event end must be after start");
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
        rules.setAllowCancellations(request.getRules().getAllowCancellations());
        rules.setIsPublic(request.getRules().getIsPublic());

        event.setRules(rules);

        Event savedEvent = eventRepository.save(event);

        slotService.generateSlots(savedEvent);

        return savedEvent;
    }

    public List<Event> getEvents() {
        return eventRepository.findAll();
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event Not Found with Id " + id));
    }

    public void deleteEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event Not Found"));

        if (!event.getHost().getId().equals(userService.getCurrentUser().getId())) {
            throw new UnauthorizedAccessException("You are not authorized to delete other user's event");
        }

        eventRepository.deleteById(id);
    }

    public Event getEventByShareableId(String shareableId) {
        return eventRepository.findByShareableId(shareableId)
                .orElseThrow(() -> new EventNotFoundException("Event Not Found"));
    }

}
