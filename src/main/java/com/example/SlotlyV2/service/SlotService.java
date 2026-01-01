package com.example.SlotlyV2.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.example.SlotlyV2.dto.BookingEmailDTO;
import com.example.SlotlyV2.dto.SlotRequest;
import com.example.SlotlyV2.event.SlotBookedEvent;
import com.example.SlotlyV2.exception.EventNotFoundException;
import com.example.SlotlyV2.exception.InvalidSlotException;
import com.example.SlotlyV2.exception.MaxCapacityExceededException;
import com.example.SlotlyV2.exception.SlotAlreadyBookedException;
import com.example.SlotlyV2.exception.SlotNotFoundException;
import com.example.SlotlyV2.model.Event;
import com.example.SlotlyV2.model.Slot;
import com.example.SlotlyV2.model.User;
import com.example.SlotlyV2.repository.EventRepository;
import com.example.SlotlyV2.repository.SlotRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SlotService {
    private final SlotRepository slotRepository;
    private final EventRepository eventRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void generateSlots(Event event) {
        LocalDateTime start = event.getEventStart();
        LocalDateTime end = event.getEventEnd();
        List<Slot> slots = new ArrayList<>();

        // TODO: Improve the slot generation algorithm
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
        // Find the slot
        Slot slot = slotRepository.findByEventIdAndStartTime(request.getEventId(), request.getStartTime())
                .orElseThrow(() -> new SlotNotFoundException("Slot Not Found"));

        // Check that the slot belongs to the event
        if (!slot.getEvent().getId().equals(request.getEventId())) {
            throw new InvalidSlotException("Slot does not belong to this event");
        }

        // Check that slot is available
        if (!slot.isAvailable()) {
            throw new SlotAlreadyBookedException("This slot is already booked. Please choose another slot");
        }

        // Check event capacity
        Integer maxCapacity = slot.getEvent().getRules().getMaxCapacity();
        if (maxCapacity != null) {
            Integer currentCapacity = slotRepository
                    .countByEventAndBookedByEmailIsNotNullAndBookedByNameIsNotNull(slot.getEvent());
            if (currentCapacity >= maxCapacity) {
                throw new MaxCapacityExceededException("This event has reached maximum capacity");
            }
        }

        // Book the Slot
        slot.setBookedByName(request.getAttendeeName());
        slot.setBookedByEmail(request.getAttendeeEmail());
        slot.setBookedAt(LocalDateTime.now());

        // Save the Slot
        Slot savedSlot = slotRepository.save(slot);

        // Prepare Booking data for emails
        String hostDisplayName = getHostDisplayName(savedSlot.getEvent().getHost());
        BookingEmailDTO bookingData = new BookingEmailDTO(
                savedSlot.getBookedByEmail(),
                savedSlot.getEvent().getHost().getEmail(),
                savedSlot.getBookedByName(),
                savedSlot.getBookedByEmail(),
                savedSlot.getEvent().getEventName(),
                savedSlot.getStartTime(),
                savedSlot.getEndTime(),
                savedSlot.getEvent().getTimeZone(),
                hostDisplayName,
                savedSlot.getId());

        // Publish the Booking Event
        eventPublisher.publishEvent(new SlotBookedEvent(bookingData));

        return savedSlot;
    }

    private String getHostDisplayName(User host) {
        String displayName = "";
        if (host.getFirstName() != null) {
            displayName += host.getFirstName();
        }
        if (host.getLastName() != null) {
            displayName += " " + host.getLastName();
        }
        return displayName.trim();
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
