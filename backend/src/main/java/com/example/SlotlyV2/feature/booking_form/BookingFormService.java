package com.example.SlotlyV2.feature.booking_form;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.SlotlyV2.common.exception.auth.ForbiddenException;
import com.example.SlotlyV2.common.exception.booking_form.BookingFormAlreadyExists;
import com.example.SlotlyV2.common.exception.booking_form.BookingFormNotFoundException;
import com.example.SlotlyV2.common.exception.event.EventNotFoundException;
import com.example.SlotlyV2.feature.booking_form.dto.BookingFormFieldRequest;
import com.example.SlotlyV2.feature.booking_form.dto.BookingFormRequest;
import com.example.SlotlyV2.feature.booking_form.dto.BookingFormResponse;
import com.example.SlotlyV2.feature.event.Event;
import com.example.SlotlyV2.feature.event.EventRepository;
import com.example.SlotlyV2.feature.user.User;
import com.example.SlotlyV2.feature.user.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingFormService {
    private final BookingFormRepository bookingFormRepository;
    private final FormAnswerRepository formAnswerRepository;
    private final EventRepository eventRepository;
    private final UserService userService;

    @Transactional
    public BookingFormResponse createForm(Long eventId, BookingFormRequest request) {
        User currentUser = userService.getCurrentUser();
        Event event = findAndAuthorizeEvent(currentUser, eventId);

        if (event.getBookingForm() != null) {
            throw new BookingFormAlreadyExists("Event already has a booking form");
        }

        BookingForm form = BookingForm.builder()
                .event(event)
                .fields(List.of())
                .build();

        form.setFields(buildFields(request, form));
        event.setBookingForm(form);

        log.info("Booking form created eventId={} userId={}", eventId, currentUser.getId());
        return toResponse(bookingFormRepository.save(form));
    }

    @Transactional
    public BookingFormResponse updateForm(Long eventId, BookingFormRequest request) {
        User currentUser = userService.getCurrentUser();
        Event event = findAndAuthorizeEvent(currentUser, eventId);

        BookingForm form = bookingFormRepository.findByEventId(eventId)
                .orElseThrow(() -> new BookingFormNotFoundException(
                        "No booking form exists for this event — create one first"));

        // Delete existing answers before clearing fields to avoid FK violations
        if (!form.getFields().isEmpty()) {
            formAnswerRepository.deleteByFormFieldIn(form.getFields());
            form.getFields().clear();
        }

        form.setFields(buildFields(request, form));
        event.setBookingForm(form);

        log.info("Booking form updated eventId={} userId={}", eventId, currentUser.getId());
        return toResponse(bookingFormRepository.save(form));
    }

    @Transactional(readOnly = true)
    public BookingFormResponse getForm(Long eventId) {
        return toResponse(bookingFormRepository.findByEventId(eventId)
                .orElseThrow(() -> new BookingFormNotFoundException("Booking form not found")));
    }

    @Transactional(readOnly = true)
    public BookingFormResponse getPublicForm(String shareableId) {
        Event event = eventRepository.findByShareableIdAndDeletedAtIsNull(shareableId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        if (!event.getAvailabilityRules().getIsPublic()) {
            throw new EventNotFoundException("Event not found"); // don't leak private event existence
        }

        return toResponse(bookingFormRepository.findByEventId(event.getId())
                .orElseThrow(() -> new BookingFormNotFoundException("Booking form not found")));
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private Event findAndAuthorizeEvent(User user, Long eventId) {
        Event event = eventRepository.findByIdAndDeletedAtIsNull(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        if (!event.getHost().getId().equals(user.getId())) {
            log.warn("Unauthorized booking form access attempt eventId={} userId={}", eventId, user.getId());
            throw new ForbiddenException("You are not authorized to access this resource");
        }

        return event;
    }

    private List<FormQuestion> buildFields(BookingFormRequest request, BookingForm form) {
        return request.getFields().stream()
                .map(q -> buildField(q, form))
                .toList();
    }

    private FormQuestion buildField(BookingFormFieldRequest dto, BookingForm form) {
        return FormQuestion.builder()
                .bookingForm(form)
                .label(dto.getLabel())
                .fieldType(dto.getFieldType())
                .required(dto.isRequired())
                .displayOrder(dto.getDisplayOrder())
                .build();
    }

    private BookingFormResponse toResponse(BookingForm form) {
        return new BookingFormResponse(form);
    }
}
