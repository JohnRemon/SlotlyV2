package com.example.SlotlyV2.feature.slot;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.SlotlyV2.common.exception.auth.UnauthorizedAccessException;
import com.example.SlotlyV2.common.exception.event.EventNotFoundException;
import com.example.SlotlyV2.common.exception.slot.SlotNotFoundException;
import com.example.SlotlyV2.common.util.SlotUtils;
import com.example.SlotlyV2.common.util.TimeZoneConverter;
import com.example.SlotlyV2.feature.event.Event;
import com.example.SlotlyV2.feature.event.EventRepository;
import com.example.SlotlyV2.feature.event.strategy.RecurrenceStrategy;
import com.example.SlotlyV2.feature.event.strategy.RecurrenceStrategyFactory;
import com.example.SlotlyV2.feature.slot.dto.CancelBookingRequest;
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
    private final SlotUtils slotUtils;
    private final RecurrenceStrategyFactory recurrenceStrategyFactory;
    private final TimeZoneConverter timeZoneConverter;

    private final SlotValidator slotValidator;
    private final BookingEventPublisher bookingEventPublisher;

    @Transactional(rollbackOn = Exception.class)
    public void generateSlots(Event event) {
        slotRepository.saveAll(slotUtils.buildSlotsByTime(event, event.getEventStart(), event.getEventEnd()));
    }

    @Transactional(rollbackOn = Exception.class)
    public void generateSlots(Event event, OffsetDateTime start, OffsetDateTime end) {
        slotRepository.saveAll(slotUtils.buildSlotsByTime(event, start, end));
    }

    @Transactional(rollbackOn = Exception.class)
    public void generateSlotsRecurring(Event event) {
        String strategyType = event.getRecurrenceRules().getRecurrenceFrequency() + "_"
                + event.getRecurrenceRules().getRecurrenceEndType();
        RecurrenceStrategy recurrenceStrategy = recurrenceStrategyFactory.getStrategy(strategyType);

        slotRepository.saveAll(recurrenceStrategy.generateSlots(event));
    }

    @Transactional(rollbackOn = Exception.class)
    public Slot bookSlot(SlotRequest request) {
        Slot slot = findSlot(request.getEventId(), request.getStartTime());

        slotValidator.validateSlotForBooking(slot);

        performBooking(request, slot);
        Slot savedSlot = slotRepository.save(slot);

        bookingEventPublisher.publishBookingEvents(savedSlot);

        return savedSlot;
    }

    @Transactional(rollbackOn = Exception.class)
    public Slot cancelBooking(CancelBookingRequest request) {
        Slot slot = findSlot(request.getEventId(), request.getStartTime());

        slotValidator.validateSlotForCancellation(slot, request.getAttendeeEmail());

        String attendeeName = slot.getBookedByName();
        String attendeeEmail = slot.getBookedByEmail();

        performCancellation(slot);
        Slot savedSlot = slotRepository.save(slot);

        bookingEventPublisher.publishCancellationEvents(slot, attendeeName, attendeeEmail);

        return savedSlot;
    }

    public List<Slot> getSlots(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException("Event Not Found");
        }

        return slotRepository.findByEventId(eventId);
    }

    public List<Slot> getBookedSlots(User user) {
        return slotRepository.findByBookedByEmail(user.getEmail());
    }

    public List<Slot> getAvailableSlotsByShareableId(String shareableId) {
        Event event = eventRepository.findByShareableId(shareableId)
                .orElseThrow(() -> new EventNotFoundException("Event Not Found"));

        if (!event.getAvailabilityRules().getIsPublic()) {
            throw new UnauthorizedAccessException("Event is private");
        }

        return slotRepository.findByEventAndBookedByEmailIsNullAndBookedByNameIsNull(event);
    }

    public Slot getSlotById(Long slotId) {
        return slotRepository.findById(slotId)
                .orElseThrow(() -> new SlotNotFoundException("Slot not found with ID: " + slotId));
    }

    private void performBooking(SlotRequest request, Slot slot) {
        slot.setBookedByName(request.getAttendeeName());
        slot.setBookedByEmail(request.getAttendeeEmail());
        slot.setBookedAt(OffsetDateTime.now());
    }

    private void performCancellation(Slot slot) {
        slot.setBookedByEmail(null);
        slot.setBookedByName(null);
    }

    private Slot findSlot(Long eventId, OffsetDateTime startTime) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event Not Found"));

        OffsetDateTime utcStartTime = timeZoneConverter.toUtc(startTime, event.getTimeZone());

        return slotRepository.findByEventIdAndStartTime(eventId, utcStartTime)
                .orElseThrow(() -> new SlotNotFoundException("Slot Not Found"));
    }
}
