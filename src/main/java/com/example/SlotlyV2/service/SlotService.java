package com.example.SlotlyV2.service;

import java.util.List;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.example.SlotlyV2.dto.SlotRequest;
import com.example.SlotlyV2.event.SlotBookedEvent;
import com.example.SlotlyV2.exception.EventNotFoundException;
import com.example.SlotlyV2.exception.SlotAlreadyBookedException;
import com.example.SlotlyV2.exception.SlotNotFoundException;
import com.example.SlotlyV2.model.Event;
import com.example.SlotlyV2.model.Slot;
import com.example.SlotlyV2.model.User;
import com.example.SlotlyV2.repository.EventRepository;
import com.example.SlotlyV2.repository.SlotRepository;

import jakarta.transaction.Transactional;

@Service
public class SlotService {
    private final SlotRepository slotRepository;
    private final EventRepository eventRepository;
    private final ApplicationEventPublisher eventPublisher;

    public SlotService(SlotRepository slotRepository, EventRepository eventRepository,
            ApplicationEventPublisher eventPublisher) {
        this.slotRepository = slotRepository;
        this.eventRepository = eventRepository;
        this.eventPublisher = eventPublisher;
    }

    public void generateSlots(Event event) {
        LocalDateTime start = event.getEventStart();
        LocalDateTime end = event.getEventEnd();
        List<Slot> slots = new ArrayList<>();

        // TODO Improve the slot generation algorithm
        while (start.plusMinutes(event.getRules().getSlotDurationMinutes()).isBefore(end)) {
            Slot slot = new Slot();
            slot.setEvent(event);
            slot.setStartTime(start);
            slot.setEndTime(start.plusMinutes(event.getRules().getSlotDurationMinutes()));
            slot.setBookedByName(null);
            slot.setBookedByEmail(null);

            slots.add(slot);
            start = start.plusMinutes(event.getRules().getSlotDurationMinutes());
        }

        slotRepository.saveAll(slots);
    }

    public List<Slot> getSlots(Long eventId) {
        return slotRepository.findByEventId(eventId);
    }

    @Transactional
    public Slot bookSlot(SlotRequest request) {
        Slot slot = slotRepository.findByEventIdAndStartTime(request.getEventId(), request.getStartTime())
                .orElseThrow(() -> new SlotNotFoundException("Slot Not Found"));

        if (!slot.isAvailable()) {
            throw new SlotAlreadyBookedException("This slot is already booked. Please choose another slot");
        }

        // TODO add more validation on slots

        slot.setBookedByName(request.getAttendeeName());
        slot.setBookedByEmail(request.getAttendeeEmail());
        slot.setBookedAt(LocalDateTime.now());

        Slot savedSlot = slotRepository.save(slot);

        eventPublisher.publishEvent(new SlotBookedEvent(savedSlot));

        return savedSlot;
    }

    public List<Slot> getBookedSlots(User user) {
        return slotRepository.findByBookedByEmail(user.getEmail());
    }

    public List<Slot> getAvailableSlotsByShareableId(String shareableId) {
        Event event = eventRepository.findByShareableId(shareableId)
                .orElseThrow(() -> new EventNotFoundException("Event Not Found"));

        return slotRepository.findByEventAndBookedByEmailIsNullAndBookedByNameIsNull(event);
    }

    public Slot getSlotById(Long slotId) {
        return slotRepository.findById(slotId)
                .orElseThrow(() -> new SlotNotFoundException("Slot not found with ID: " + slotId));

    }
}
