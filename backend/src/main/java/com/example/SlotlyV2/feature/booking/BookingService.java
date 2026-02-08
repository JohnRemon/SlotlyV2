package com.example.SlotlyV2.feature.booking;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.SlotlyV2.common.exception.booking.BookingNotFoundException;
import com.example.SlotlyV2.common.exception.booking_form.QuestionNotFoundException;
import com.example.SlotlyV2.common.exception.event.EventNotFoundException;
import com.example.SlotlyV2.common.exception.slot.SlotNotFoundException;
import com.example.SlotlyV2.feature.booking.dto.BookingRequest;
import com.example.SlotlyV2.feature.booking.dto.CancelBookingRequest;
import com.example.SlotlyV2.feature.booking_form.BookingFormValidator;
import com.example.SlotlyV2.feature.booking_form.FieldAnswer;
import com.example.SlotlyV2.feature.booking_form.FormQuestion;
import com.example.SlotlyV2.feature.booking_form.FormQuestionRepository;
import com.example.SlotlyV2.feature.booking_form.dto.FieldAnswerDTO;
import com.example.SlotlyV2.feature.event.Event;
import com.example.SlotlyV2.feature.event.EventRepository;
import com.example.SlotlyV2.feature.slot.Slot;
import com.example.SlotlyV2.feature.slot.SlotRepository;
import com.example.SlotlyV2.feature.slot.SlotValidator;
import com.example.SlotlyV2.feature.user.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final SlotRepository slotRepository;
    private final EventRepository eventRepository;
    private final FormQuestionRepository formQuestionRepository;
    private final SlotValidator slotValidator;
    private final BookingEventPublisher bookingEventPublisher;
    private final BookingFormValidator bookingFormValidator;

    @Transactional
    public Booking book(BookingRequest request) {
        Slot slot = slotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new SlotNotFoundException("Slot not found"));

        slotValidator.validateSlotForBooking(slot);

        Booking booking = bookingRepository.save(buildBooking(request, slot));

        bookingEventPublisher.publishBookingEvents(booking);

        return booking;
    }

    @Transactional
    public void cancel(CancelBookingRequest request, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        slotValidator.validateSlotForCancellation(booking, request.getAttendeeEmail());

        booking.cancel(request.getCancellationReason());

        bookingEventPublisher.publishCancellationEvents(booking);
    }

    public List<Booking> getBookings(User user) {
        return bookingRepository.findByAttendeeEmail(user.getEmail());
    }

    private Booking buildBooking(BookingRequest request, Slot slot) {
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        Booking booking = Booking.builder()
                .slot(slot)
                .event(event)
                .attendeeName(request.getAttendeeName())
                .attendeeEmail(request.getAttendeeEmail())
                .notes(request.getNotes())
                .build();

        if (event.getBookingForm() != null) {
            buildAnswers(booking, request);
        }

        return booking;

    }

    public void buildAnswers(Booking booking, BookingRequest bookingRequest) {
        bookingFormValidator.validateAnswers(booking.getEvent().getBookingForm().getFields(),
                bookingRequest.getFormSubmission().getAnswers());

        bookingRequest.getFormSubmission().getAnswers().stream()
                .map(answer -> buildAndAddAnswer(booking, answer))
                .collect(Collectors.toList());
    }

    private FieldAnswer buildAndAddAnswer(Booking booking, FieldAnswerDTO dto) {
        FieldAnswer fieldAnswer = buildAnswer(dto);
        booking.addFormAnswer(fieldAnswer);
        return fieldAnswer;
    }

    private FieldAnswer buildAnswer(FieldAnswerDTO dto) {
        return FieldAnswer.builder()
                .formField(findQuestionById(dto.getFieldId()))
                .answer(dto.getFieldResponse())
                .build();
    }

    private FormQuestion findQuestionById(UUID questionId) {
        return formQuestionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException("Field not found"));
    }
}
