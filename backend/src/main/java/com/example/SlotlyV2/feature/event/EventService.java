package com.example.SlotlyV2.feature.event;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.SlotlyV2.common.exception.auth.UnauthorizedAccessException;
import com.example.SlotlyV2.common.exception.event.EventNotFoundException;
import com.example.SlotlyV2.common.exception.schedule.InvalidScheduleException;
import com.example.SlotlyV2.common.exception.schedule.ScheduleNotFoundException;
import com.example.SlotlyV2.common.util.TimeZoneConverter;
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
import com.example.SlotlyV2.feature.event.dto.PublicEventResponse;
import com.example.SlotlyV2.feature.schedule.Schedule;
import com.example.SlotlyV2.feature.schedule.ScheduleRepository;
import com.example.SlotlyV2.feature.slot.SlotRepository;
import com.example.SlotlyV2.feature.slot.SlotService;
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
    public EventResponse createEvent(EventRequest request) {
        eventValidator.validateEventDates(request.getEventStart(), request.getEventEnd());
        Schedule defaultSchedule = getDefaultSchedule();
        Event event = eventRepository.save(eventFactory.createFrom(request, defaultSchedule));
        slotService.generateSlots(event, defaultSchedule);
        log.info("Event created eventId={} userId={}", event.getId(), userService.getCurrentUser().getId());
        return toResponse(event);
    }

    @Transactional
    public EventResponse createRecurringEvent(EventRequest request) {
        eventValidator.validateEventDates(request.getEventStart(), request.getEventEnd());
        eventValidator.validateRecurringEventRules(request);
        Schedule defaultSchedule = getDefaultSchedule();
        Event event = eventRepository.save(eventFactory.createFrom(request, defaultSchedule));
        slotService.generateSlotsRecurring(event, defaultSchedule);
        log.info("Recurring event created eventId={} userId={}", event.getId(), userService.getCurrentUser().getId());
        return toResponse(event);
    }

    public Page<EventResponse> getEvents(Pageable pageable) {
        return eventRepository.findByHostAndDeletedAtIsNull(userService.getCurrentUser(), pageable)
                .map(this::toResponse);
    }

    public EventResponse getEventById(Long id) {
        return toResponse(findAndAuthorizeEvent(id));
    }

    public PublicEventResponse getPublicEvent(String shareableId) {
        Event event = eventRepository.findByShareableIdAndDeletedAtIsNull(shareableId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        return new PublicEventResponse(event, timeZoneConverter);
    }

    public Page<EventResponse> getEventsByScheduleId(UUID scheduleId, Pageable pageable) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found"));

        return eventRepository.findByScheduleAndDeletedAtIsNull(schedule, pageable)
                .map(this::toResponse);
    }

    @Transactional
    public EventResponse updateEvent(EventRequest request, Long id) {
        Event event = findAndAuthorizeEvent(id);
        validateNewCapacity(request, event);
        updateEventDetails(request, event);
        slotService.regenerateFutureSlots(event);
        return toResponse(eventRepository.save(event));
    }

    @Transactional
    public EventResponse updateAvailabilityRules(AvailabilityRulesUpdateRequest request, Long id) {
        Event event = findAndAuthorizeEvent(id);
        event.setAvailabilityRules(eventFactory.buildAvailabilityRules(request));
        slotService.regenerateFutureSlots(event);
        return toResponse(eventRepository.save(event));
    }

    @Transactional
    public EventResponse updateBookingForm(BookingFormUpdateRequest request, Long id) {
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
                        .displayOrder(fieldReq.getDisplayOrder() != null
                                ? fieldReq.getDisplayOrder()
                                : bookingForm.getFields().size())
                        .build();
                bookingForm.getFields().add(field);
            }
        }

        return toResponse(eventRepository.save(event));
    }

    @Transactional
    public EventResponse updateEventSchedule(UUID scheduleId, Long id) {
        Event event = findAndAuthorizeEvent(id);
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found"));

        if (!schedule.getUser().getId().equals(userService.getCurrentUser().getId())) {
            throw new UnauthorizedAccessException("Not your schedule");
        }

        event.setSchedule(schedule);
        slotService.regenerateFutureSlots(event);
        return toResponse(eventRepository.save(event));
    }

    @Transactional
    public void deleteEventById(Long id) {
        Event event = findAndAuthorizeEvent(id);
        OffsetDateTime nowUtc = OffsetDateTime.now(ZoneOffset.UTC);

        List<Booking> upcomingBookings = bookingRepository
                .findByEventIdAndSlotEndTimeGreaterThanEqual(event.getId(), nowUtc);
        List<Long> upcomingBookingIds = upcomingBookings.stream().map(Booking::getId).toList();

        EventCancelledEmailDTO data = new EventCancelledEmailDTO(
                event.getId(),
                event.getEventName(),
                upcomingBookings.stream()
                        .filter(Booking::isActive)
                        .map(Booking::getAttendeeEmail)
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
        log.info("Event deleted eventId={} userId={}", event.getId(), userService.getCurrentUser().getId());
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private Event findAndAuthorizeEvent(Long id) {
        Event event = eventRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        if (!event.getHost().getId().equals(userService.getCurrentUser().getId())) {
            throw new UnauthorizedAccessException("You are not authorized to access this event");
        }

        return event;
    }

    private void validateNewCapacity(EventRequest request, Event event) {
        Integer booked = bookingRepository.countByEventAndStatus(event, BookingStatus.CONFIRMED);
        eventValidator.validateNewCapacity(request.getAvailabilityRulesDTO().getMaxCapacity(), booked);
    }

    private void updateEventDetails(EventRequest request, Event event) {
        event.setEventName(request.getEventName());
        event.setDescription(request.getDescription());
        event.setAvailabilityRules(eventFactory.buildAvailabilityRules(request.getAvailabilityRulesDTO()));
    }

    private Schedule getDefaultSchedule() {
        return scheduleRepository.findByUserAndIsDefaultTrue(userService.getCurrentUser())
                .orElseThrow(() -> new InvalidScheduleException("No default schedule found"));
    }

    private EventResponse toResponse(Event event) {
        return new EventResponse(event, timeZoneConverter);
    }
}
