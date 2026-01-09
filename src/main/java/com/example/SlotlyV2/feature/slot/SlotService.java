package com.example.SlotlyV2.feature.slot;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.example.SlotlyV2.common.exception.auth.UnauthorizedAccessException;
import com.example.SlotlyV2.common.exception.event.EventNotFoundException;
import com.example.SlotlyV2.common.exception.event.MaxCapacityExceededException;
import com.example.SlotlyV2.common.exception.slot.InvalidSlotException;
import com.example.SlotlyV2.common.exception.slot.SlotAlreadyBookedException;
import com.example.SlotlyV2.common.exception.slot.SlotNotFoundException;
import com.example.SlotlyV2.common.util.NameUtils;
import com.example.SlotlyV2.feature.email.dto.BookingEmailDTO;
import com.example.SlotlyV2.feature.email.event.SlotBookedEvent;
import com.example.SlotlyV2.feature.email.event.SlotCancelledEvent;
import com.example.SlotlyV2.feature.event.Event;
import com.example.SlotlyV2.feature.event.EventRepository;
import com.example.SlotlyV2.feature.slot.dto.CancelBookingRequest;
import com.example.SlotlyV2.feature.slot.dto.SlotCancelledEmailDTO;
import com.example.SlotlyV2.feature.slot.dto.SlotRequest;
import com.example.SlotlyV2.feature.user.User;

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
    private final NameUtils nameUtils;

    @Transactional(rollbackOn = Exception.class)
    public void generateSlots(Event event) {
        LocalDateTime start = event.getEventStart();
        LocalDateTime end = event.getEventEnd();
        List<Slot> slots = new ArrayList<>();

        if (event.getRules().getSlotDurationMinutes() <= 0) {
            throw new InvalidSlotException("Slot duration must be greater than zero");
        }

        // TODO: Improve the slot generation algorithm
        while (!start.isAfter(end.minusMinutes(event.getRules().getSlotDurationMinutes()))) {
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

    @Transactional(rollbackOn = Exception.class)
    public Slot bookSlot(SlotRequest request) {
        // Find the slot
        Slot slot = slotRepository.findByEventIdAndStartTime(request.getEventId(), request.getStartTime())
                .orElseThrow(() -> new SlotNotFoundException("Slot Not Found"));

        // Check that slot is available
        if (!slot.isAvailable()) {
            throw new SlotAlreadyBookedException("This slot is already booked. Please choose another slot");
        }

        // Check that slot is not in the past
        ZoneId zone = ZoneId.of(slot.getEvent().getTimeZone());
        if (slot.getStartTime().atZone(zone).isBefore(ZonedDateTime.now(zone))) {
            throw new InvalidSlotException("Cannot book a past slot");
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
        String hostDisplayName = nameUtils.getUserDisplayName(savedSlot);
        BookingEmailDTO bookingData = BookingEmailDTO.builder()
                .attendeeEmail(savedSlot.getBookedByEmail())
                .hostEmail(savedSlot.getEvent().getHost().getEmail())
                .attendeeName(savedSlot.getBookedByName())
                .eventName(savedSlot.getEvent().getEventName())
                .startTime(savedSlot.getStartTime())
                .endTime(savedSlot.getEndTime())
                .timeZone(savedSlot.getEvent().getTimeZone())
                .hostDisplayName(hostDisplayName)
                .slotId(savedSlot.getId())
                .build();

        // Publish the Booking Event
        eventPublisher.publishEvent(new SlotBookedEvent(bookingData));

        return savedSlot;
    }

    @Transactional(rollbackOn = Exception.class)
    public Slot cancelBooking(CancelBookingRequest request) {
        // Find the slot
        Slot slot = slotRepository.findByEventIdAndStartTime(request.getEventId(), request.getStartTime())
                .orElseThrow(() -> new SlotNotFoundException("Slot Not Found"));

        if (!slot.getEvent().getRules().getAllowsCancellations()) {
            throw new InvalidSlotException("Cancellations are not allowed for this event");
        }

        ZoneId zone = ZoneId.of(slot.getEvent().getTimeZone());
        if (slot.getStartTime().atZone(zone).isBefore(ZonedDateTime.now(zone))) {
            throw new InvalidSlotException("Cannot cancel a past slot");
        }

        if (slot.isAvailable()) {
            throw new InvalidSlotException("This slot is not booked");
        }

        if (!slot.getBookedByEmail().equals(request.getAttendeeEmail())) {
            throw new UnauthorizedAccessException("This email is not associated with the booked slot");
        }

        String attendeeName = slot.getBookedByName();
        String attendeeEmail = slot.getBookedByEmail();

        // Cancel the booking
        slot.setBookedByEmail(null);
        slot.setBookedByName(null);

        // Save the Slot
        Slot savedSlot = slotRepository.save(slot);

        // Prepare Cancellation data
        String hostDisplayName = nameUtils.getUserDisplayName(savedSlot);

        SlotCancelledEmailDTO cancellationData = new SlotCancelledEmailDTO(
                savedSlot.getId(),
                savedSlot.getStartTime().toString(),
                savedSlot.getEndTime().toString(),
                attendeeName,
                attendeeEmail,
                savedSlot.getEvent().getEventName(),
                hostDisplayName,
                savedSlot.getEvent().getHost().getEmail());

        // Publish the Cancellation Event
        eventPublisher.publishEvent(new SlotCancelledEvent(cancellationData));

        return savedSlot;
    }

    public List<Slot> getSlots(Long eventId) {
        return slotRepository.findByEventId(eventId);
    }

    public List<Slot> getBookedSlots(User user) {
        return slotRepository.findByBookedByEmail(user.getEmail());
    }

    public List<Slot> getAvailableSlotsByShareableId(String shareableId) {

        Event event = eventRepository.findByShareableId(shareableId)
                .orElseThrow(() -> new EventNotFoundException("Event Not Found"));

        if (!event.getRules().getIsPublic()) {
            throw new UnauthorizedAccessException("Event is private");
        }

        return slotRepository.findByEventAndBookedByEmailIsNullAndBookedByNameIsNull(event);
    }

    public Slot getSlotById(Long slotId) {
        return slotRepository.findById(slotId)
                .orElseThrow(() -> new SlotNotFoundException("Slot not found with ID: " + slotId));

    }
}
