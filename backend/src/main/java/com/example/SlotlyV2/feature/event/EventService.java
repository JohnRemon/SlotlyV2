package com.example.SlotlyV2.feature.event;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.SlotlyV2.common.exception.auth.UnauthorizedAccessException;
import com.example.SlotlyV2.common.exception.event.EventNotFoundException;
import com.example.SlotlyV2.common.exception.schedule.InvalidScheduleException;
import com.example.SlotlyV2.common.exception.schedule.ScheduleNotFoundException;
import com.example.SlotlyV2.common.util.TimeZoneConverter;
import com.example.SlotlyV2.feature.availability.AvailabilityRules;
import com.example.SlotlyV2.feature.availability.dto.AvailabilityRulesUpdateRequest;
import com.example.SlotlyV2.feature.booking.Booking;
import com.example.SlotlyV2.feature.booking.BookingRepository;
import com.example.SlotlyV2.feature.booking.BookingStatus;
import com.example.SlotlyV2.feature.booking_form.BookingForm;
import com.example.SlotlyV2.feature.booking_form.FormQuestion;
import com.example.SlotlyV2.feature.booking_form.dto.BookingFormUpdateRequest;
import com.example.SlotlyV2.feature.booking_form.enums.FieldType;
import com.example.SlotlyV2.feature.calendar.BookingGoogleEventRepository;
import com.example.SlotlyV2.feature.email.dto.EventCancelledEmailDTO;
import com.example.SlotlyV2.feature.email.event.EventCancelledEvent;
import com.example.SlotlyV2.feature.event.dto.EventRequest;
import com.example.SlotlyV2.feature.event.dto.EventResponse;
import com.example.SlotlyV2.feature.schedule.Schedule;
import com.example.SlotlyV2.feature.schedule.ScheduleRepository;
import com.example.SlotlyV2.feature.slot.SlotRepository;
import com.example.SlotlyV2.feature.slot.SlotService;
import com.example.SlotlyV2.feature.user.User;
import com.example.SlotlyV2.feature.user.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    private final SlotRepository slotRepository;
    private final ScheduleRepository scheduleRepository;
    private final BookingGoogleEventRepository bookingGoogleEventRepository;
    private final SlotService slotService;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;
    private final TimeZoneConverter timeZoneConverter;

    private final EventValidator eventValidator;
    private final EventFactory eventFactory;

    @Transactional
    public Event createEvent(EventRequest request) {
        // Verify Start and End Dates
        eventValidator.validateEventDates(request.getEventStart(), request.getEventEnd());

        // Fetch default schedule
        Schedule defaultSchedule = scheduleRepository.findByUserAndIsDefaultTrue(userService.getCurrentUser())
                .orElseThrow(() -> new InvalidScheduleException("No default schedule found"));

        // Create the Event
        Event event = eventFactory.createFrom(request, defaultSchedule);
        event = eventRepository.save(event);

        // Generate slots
        slotService.generateSlots(event, defaultSchedule);

        return event;
    }

    @Transactional
    public Event createRecurringEvent(EventRequest request) {
        // Validate the Event
        eventValidator.validateEventDates(request.getEventStart(), request.getEventEnd());
        eventValidator.validateRecurringEventRules(request);

        // Fetch default schedule
        Schedule defaultSchedule = scheduleRepository.findByUserAndIsDefaultTrue(userService.getCurrentUser())
                .orElseThrow(() -> new InvalidScheduleException("No default schedule found"));

        // Create the event
        Event event = eventFactory.createFrom(request, defaultSchedule);
        event = eventRepository.save(event);

        // Generate slots
        slotService.generateSlotsRecurring(event, defaultSchedule);

        return event;
    }

    public Page<EventResponse> getEvents(User host, Pageable pageable) {
        Page<Event> eventPage = eventRepository.findByHostAndDeletedAtIsNull(host, pageable);

        List<EventResponse> responses = eventPage.getContent().stream()
                .map(event -> new EventResponse(event, timeZoneConverter))
                .toList();

        return new PageImpl<>(responses, pageable, eventPage.getTotalElements());
    }

    public Event getEventById(Long id) {
        return findAndAuthorizeEvent(id);
    }

    public Event getEventByShareableId(String shareableId) {
        Event event = eventRepository.findByShareableIdAndDeletedAtIsNull(shareableId)
                .orElseThrow(() -> new EventNotFoundException("Event Not Found"));

        return event;
    }

    @Transactional
    public Event updateEvent(EventRequest request, Long id) {
        Event event = findAndAuthorizeEvent(id);

        validateNewCapacity(request, event);

        updateEventDetails(request, event);

        slotService.regenerateFutureSlots(event);

        return eventRepository.save(event);
    }

    @Transactional
    public Event updateAvailabilityRules(AvailabilityRulesUpdateRequest request, Long id) {
        Event event = findAndAuthorizeEvent(id);

        AvailabilityRules newRules = eventFactory.buildAvailabilityRules(request);
        event.setAvailabilityRules(newRules);

        slotService.regenerateFutureSlots(event);

        return eventRepository.save(event);
    }

    @Transactional
    public Event updateBookingForm(BookingFormUpdateRequest request, Long id) {
        Event event = findAndAuthorizeEvent(id);

        BookingForm bookingForm = event.getBookingForm();

        if (request.getFields() != null) {
            bookingForm.getFields().clear();
            for (var fieldReq : request.getFields()) {
                FormQuestion field = FormQuestion.builder()
                        .bookingForm(bookingForm)
                        .label(fieldReq.getLabel())
                        .fieldType(fieldReq.getFieldType() != null ? fieldReq.getFieldType() : FieldType.TEXT)
                        .required(fieldReq.isRequired())
                        .displayOrder(fieldReq.getDisplayOrder() != null ? fieldReq.getDisplayOrder()
                                : bookingForm.getFields().size())
                        .build();
                bookingForm.getFields().add(field);
            }
        }

        event.setBookingForm(bookingForm);
        return eventRepository.save(event);
    }

    @Transactional
    public Event updateEventSchedule(UUID scheduleId, Long id) {
        Event event = findAndAuthorizeEvent(id);

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found"));

        if (!schedule.getUser().getId().equals(userService.getCurrentUser().getId())) {
            throw new UnauthorizedAccessException("Not your schedule");
        }

        event.setSchedule(schedule);
        eventRepository.save(event);

        slotService.regenerateFutureSlots(event);

        return event;
    }

    @Transactional
    public void deleteEventById(Long id) {
        Event event = findAndAuthorizeEvent(id);
        OffsetDateTime nowUtc = OffsetDateTime.now(ZoneOffset.UTC);

        List<Booking> upcomingBookings = bookingRepository.findByEventIdAndSlotEndTimeGreaterThanEqual(event.getId(),
                nowUtc);
        List<Long> upcomingBookingIds = upcomingBookings.stream().map(Booking::getId).toList();

        // Build Cancellation Email
        EventCancelledEmailDTO data = new EventCancelledEmailDTO(
                event.getId(),
                event.getEventName(),
                upcomingBookings.stream()
                        .filter(Booking::isActive)
                        .map(booking -> booking.getAttendeeEmail())
                        .distinct()
                        .toList());

        if (!upcomingBookingIds.isEmpty()) {
            bookingGoogleEventRepository.deleteByBookingIdIn(upcomingBookingIds);
            bookingRepository.deleteAll(upcomingBookings);
        }
        slotRepository.deleteByEventIdAndEndTimeGreaterThanEqual(event.getId(), nowUtc);

        event.markDeleted(nowUtc);
        eventRepository.save(event);
        eventPublisher.publishEvent(new EventCancelledEvent(data));
    }

    private Event findAndAuthorizeEvent(Long id) {
        Event event = eventRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new EventNotFoundException("Event Not Found"));

        validateHost(event);
        return event;
    }

    private void validateHost(Event event) {
        if (!event.getHost().getId().equals(userService.getCurrentUser().getId())) {
            throw new UnauthorizedAccessException("You are not authorized to access other user's event");
        }
    }

    private void validateNewCapacity(EventRequest request, Event event) {
        Integer booked = bookingRepository.countByEventAndStatus(event, BookingStatus.CONFIRMED);
        eventValidator.validateNewCapacity(request.getAvailabilityRulesDTO().getMaxCapacity(), booked);
    }

    private void updateEventDetails(EventRequest request, Event event) {
        event.setEventName(request.getEventName());
        event.setDescription(request.getDescription());
        // event.setTimeZone(request.getTimeZone());
        event.setAvailabilityRules(
                eventFactory.buildAvailabilityRules(request.getAvailabilityRulesDTO()));
    }

}
