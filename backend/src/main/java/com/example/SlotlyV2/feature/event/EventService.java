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

import com.example.SlotlyV2.common.exception.auth.ForbiddenException;
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
    public EventResponse createEvent(EventRequest request) {
        User currentUser = userService.getCurrentUser();
        eventValidator.validateEventDates(request.getEventStart(), request.getEventEnd());
        Schedule defaultSchedule = getDefaultSchedule(currentUser);
        Event event = eventRepository.save(eventFactory.createFrom(request, defaultSchedule, currentUser));
        slotService.generateSlots(event, defaultSchedule);
        log.info("Event created eventId={} userId={}", event.getId(), currentUser.getId());
        return toResponse(event);
    }

    @Transactional
    public EventResponse createRecurringEvent(EventRequest request) {
        User currentUser = userService.getCurrentUser();
        eventValidator.validateEventDates(request.getEventStart(), request.getEventEnd());
        eventValidator.validateRecurringEventRules(request);
        Schedule defaultSchedule = getDefaultSchedule(currentUser);
        Event event = eventRepository.save(eventFactory.createFrom(request, defaultSchedule, currentUser));
        slotService.generateSlotsRecurring(event, defaultSchedule);
        log.info("Recurring event created eventId={} userId={}", event.getId(), currentUser.getId());
        return toResponse(event);
    }

    @Transactional(readOnly = true)
    public Page<EventResponse> getEvents(Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        return eventRepository.findByHostAndDeletedAtIsNull(currentUser, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public EventResponse getEventById(Long id) {
        User currentUser = userService.getCurrentUser();
        return toResponse(findAndAuthorizeEvent(currentUser, id));
    }

    @Transactional(readOnly = true)
    public PublicEventResponse getPublicEvent(String shareableId) {
        Event event = eventRepository.findByShareableIdAndDeletedAtIsNull(shareableId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        if (!event.getAvailabilityRules().getIsPublic()) {
            throw new EventNotFoundException("Event not found");
        }

        return new PublicEventResponse(event, timeZoneConverter);
    }

    @Transactional(readOnly = true)
    public Page<EventResponse> getEventsByScheduleId(UUID scheduleId, Pageable pageable) {
        return eventRepository.findByScheduleIdAndDeletedAtIsNull(scheduleId, pageable)
                .map(this::toResponse);
    }

    @Transactional
    public EventResponse updateEvent(EventRequest request, Long id) {
        User currentUser = userService.getCurrentUser();
        Event event = findAndAuthorizeEvent(currentUser, id);
        int booked = bookingRepository.countByEventAndStatus(event, BookingStatus.CONFIRMED);
        eventValidator.validateNewCapacity(request.getAvailabilityRules().getMaxCapacity(), booked);
        updateEventDetails(request, event);
        slotService.regenerateFutureSlots(event);
        log.info("Event updated eventId={} userId={}", event.getId(), currentUser.getId());
        return toResponse(eventRepository.save(event));
    }

    @Transactional
    public EventResponse updateAvailabilityRules(AvailabilityRulesUpdateRequest request, Long id) {
        User currentUser = userService.getCurrentUser();
        Event event = findAndAuthorizeEvent(currentUser, id);
        event.setAvailabilityRules(eventFactory.buildAvailabilityRules(request));
        slotService.regenerateFutureSlots(event);
        log.info("Event availability rules updated eventId={} userId={}", event.getId(), currentUser.getId());
        return toResponse(eventRepository.save(event));
    }

    @Transactional
    public EventResponse updateBookingForm(BookingFormUpdateRequest request, Long id) {
        User currentUser = userService.getCurrentUser();
        Event event = findAndAuthorizeEvent(currentUser, id);
        BookingForm bookingForm = event.getBookingForm();

        bookingForm.getFields().clear();
        int order = 0;
        for (var fieldReq : request.getFields()) {
            FormQuestion field = FormQuestion.builder()
                    .bookingForm(bookingForm)
                    .label(fieldReq.getLabel())
                    .fieldType(fieldReq.getFieldType() != null ? fieldReq.getFieldType() : FieldType.TEXT)
                    .required(fieldReq.isRequired())
                    .displayOrder(fieldReq.getDisplayOrder() != null ? fieldReq.getDisplayOrder() : order)
                    .build();
            bookingForm.getFields().add(field);
            order++;
        }

        log.info("Event booking form updated eventId={} bookingFormId={} userId={}", event.getId(), bookingForm.getId(),
                currentUser.getId());
        return toResponse(eventRepository.save(event));
    }

    @Transactional
    public EventResponse updateEventSchedule(UUID scheduleId, Long id) {
        User currentUser = userService.getCurrentUser();
        Event event = findAndAuthorizeEvent(currentUser, id);
        Schedule schedule = findAndAuthorizeSchedule(currentUser, scheduleId);

        event.setSchedule(schedule);
        slotService.regenerateFutureSlots(event);
        log.info("Event schedule updated eventId={} scheduleId={} userId={}", event.getId(), scheduleId,
                currentUser.getId());
        return toResponse(eventRepository.save(event));
    }

    @Transactional
    public void deleteEventById(Long id) {
        User currentUser = userService.getCurrentUser();
        Event event = findAndAuthorizeEvent(currentUser, id);
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
        log.info("Event deleted eventId={} userId={}", event.getId(), currentUser.getId());
    }

    // -- Private helpers -------------------------------------------------------

    private Event findAndAuthorizeEvent(User user, Long id) {
        Event event = eventRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        if (!event.getHost().getId().equals(user.getId())) {
            log.warn("Unauthorized event access attempt eventId={} userId={}", id, user.getId());
            throw new ForbiddenException("You are not authorized to access this resource");
        }

        return event;
    }

    private Schedule findAndAuthorizeSchedule(User user, UUID scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found"));

        if (!schedule.getUser().getId().equals(user.getId())) {
            log.warn("Unauthorized schedule access attempt scheduleId={} userId={}", scheduleId, user.getId());
            throw new ForbiddenException("You are not authorized to access this resource");
        }
        return schedule;
    }

    private void updateEventDetails(EventRequest request, Event event) {
        event.setEventName(request.getEventName());
        event.setDescription(request.getDescription());
        event.setAvailabilityRules(eventFactory.buildAvailabilityRules(request.getAvailabilityRules()));
    }

    private Schedule getDefaultSchedule(User user) {
        return scheduleRepository.findByUserAndIsDefaultTrue(user)
                .orElseThrow(() -> new InvalidScheduleException("No default schedule found"));
    }

    private EventResponse toResponse(Event event) {
        return new EventResponse(event, timeZoneConverter);
    }
}
