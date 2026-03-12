package com.example.SlotlyV2.feature.booking;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.SlotlyV2.common.exception.booking.BookingNotFoundException;
import com.example.SlotlyV2.common.exception.booking_form.InvalidFormResponseException;
import com.example.SlotlyV2.common.exception.booking_form.QuestionNotFoundException;
import com.example.SlotlyV2.common.exception.slot.SlotNotFoundException;
import com.example.SlotlyV2.common.util.TimeZoneConverter;
import com.example.SlotlyV2.feature.booking.dto.BookingRequest;
import com.example.SlotlyV2.feature.booking.dto.BookingResponse;
import com.example.SlotlyV2.feature.booking.dto.CancelBookingRequest;
import com.example.SlotlyV2.feature.booking_form.BookingFormRepository;
import com.example.SlotlyV2.feature.booking_form.BookingFormValidator;
import com.example.SlotlyV2.feature.booking_form.FieldAnswer;
import com.example.SlotlyV2.feature.booking_form.FormQuestion;
import com.example.SlotlyV2.feature.booking_form.FormQuestionRepository;
import com.example.SlotlyV2.feature.booking_form.dto.BookingFormAnswerRequest;
import com.example.SlotlyV2.feature.slot.Slot;
import com.example.SlotlyV2.feature.slot.SlotRepository;
import com.example.SlotlyV2.feature.slot.SlotValidator;
import com.example.SlotlyV2.feature.user.User;
import com.example.SlotlyV2.feature.user.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {
    private final BookingRepository bookingRepository;
    private final SlotRepository slotRepository;
    private final BookingFormRepository bookingFormRepository;
    private final FormQuestionRepository formQuestionRepository;
    private final SlotValidator slotValidator;
    private final BookingEventPublisher bookingEventPublisher;
    private final BookingFormValidator bookingFormValidator;
    private final UserService userService;
    private final TimeZoneConverter timeZoneConverter;

    @Transactional
    public BookingResponse book(BookingRequest request, String timeZone) {
        Slot slot = slotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new SlotNotFoundException("Slot not found with id: " + request.getSlotId()));

        slotValidator.validateSlotForBooking(slot);

        Booking booking = bookingRepository.save(buildBooking(request, slot));
        bookingEventPublisher.publishBookingEvents(booking);

        log.info("Booking created bookingId={} slotId={} attendeeEmail={}",
                booking.getId(), slot.getId(), booking.getAttendeeEmail());
        return toResponse(booking, timeZone);
    }

    @Transactional
    public void cancelBooking(Long id, CancelBookingRequest request) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(
                        () -> new BookingNotFoundException("Booking not found with id: " + id));

        slotValidator.validateSlotForCancellation(booking, request.getAttendeeEmail());
        booking.cancel(request.getCancellationReason());
        bookingEventPublisher.publishCancellationEvents(booking);

        log.info("Booking cancelled bookingId={} attendeeEmail={}", id, request.getAttendeeEmail());
    }

    @Transactional
    public void markBookingNoShow(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + id));

        booking.markAsNoShow();
        bookingRepository.save(booking);
        log.info("Booking marked as no-show bookingId={}", id);
    }

    @Transactional(readOnly = true)
    public Page<BookingResponse> getBookings(Pageable pageable, String timeZone) {
        User currentUser = userService.getCurrentUser();
        return bookingRepository.findByAttendeeEmail(currentUser.getEmail(), pageable)
                .map(booking -> toResponse(booking, timeZone));
    }

    @Transactional(readOnly = true)
    public BookingResponse getBooking(Long id, String timeZone) {
        return toResponse(bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + id)), timeZone);
    }

    // -- Private helpers -------------------------------------------------------

    private Booking buildBooking(BookingRequest request, Slot slot) {
        Booking booking = Booking.builder()
                .slot(slot)
                .event(slot.getEvent())
                .attendeeName(request.getAttendeeName())
                .attendeeEmail(request.getAttendeeEmail())
                .notes(request.getNotes())
                .build();

        boolean hasFormSubmission = request.getFormSubmission() != null
                && request.getFormSubmission().getAnswers() != null
                && !request.getFormSubmission().getAnswers().isEmpty();

        if (hasFormSubmission) {
            List<FormQuestion> formFields = bookingFormRepository.findByEventId(slot.getEvent().getId())
                    .orElseThrow(() -> new InvalidFormResponseException("No booking form exists for this event"))
                    .getFields();

            bookingFormValidator.validateAnswers(formFields, request.getFormSubmission().getAnswers());

            request.getFormSubmission().getAnswers()
                    .forEach(answer -> booking.addFormAnswer(buildAnswer(answer)));
        }

        return booking;
    }

    private FieldAnswer buildAnswer(BookingFormAnswerRequest dto) {
        FormQuestion question = formQuestionRepository.findById(dto.getFieldId())
                .orElseThrow(() -> new QuestionNotFoundException("Field not found with id: " + dto.getFieldId()));

        return FieldAnswer.builder()
                .formField(question)
                .answer(dto.getFieldResponse())
                .build();
    }

    private BookingResponse toResponse(Booking booking, String timeZone) {
        return new BookingResponse(booking, timeZoneConverter, timeZone);
    }
}
