package com.example.SlotlyV2.feature.booking_form;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.SlotlyV2.common.exception.auth.UnauthorizedAccessException;
import com.example.SlotlyV2.common.exception.booking_form.BookingFormAlreadyExists;
import com.example.SlotlyV2.common.exception.booking_form.BookingFormNotFoundException;
import com.example.SlotlyV2.common.exception.booking_form.QuestionNotFoundException;
import com.example.SlotlyV2.common.exception.event.EventNotFoundException;
import com.example.SlotlyV2.feature.booking_form.dto.FieldAnswerDTO;
import com.example.SlotlyV2.feature.booking_form.dto.FormQuestionDTO;
import com.example.SlotlyV2.feature.booking_form.dto.FormRequest;
import com.example.SlotlyV2.feature.booking_form.dto.SubmitFormAnswers;
import com.example.SlotlyV2.feature.event.Event;
import com.example.SlotlyV2.feature.event.EventRepository;
import com.example.SlotlyV2.feature.slot.Slot;
import com.example.SlotlyV2.feature.user.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingFormService {
    private final BookingFormRepository bookingFormRepository;
    private final EventRepository eventRepository;
    private final FormAnswerRepository formAnswerRepository;
    private final FormQuestionRepository formQuestionRepository;
    private final BookingFormValidator bookingFormValidator;
    private final UserService userService;

    @Transactional
    public BookingForm createForm(Long eventId, FormRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        validateHost(event);

        if (event.getBookingForm() != null) {
            throw new BookingFormAlreadyExists("Event already has a booking form");
        }

        BookingForm form = BookingForm.builder()
                .event(event)
                .build();

        List<FormQuestion> fields = request.getFormFields().stream()
                .map(q -> buildField(q, form))
                .collect(Collectors.toList());

        form.setFields(fields);
        return bookingFormRepository.save(form);
    }

    @Transactional
    public BookingForm updateForm(Long eventId, FormRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        validateHost(event);

        BookingForm bookingForm = bookingFormRepository.findByEventId(eventId)
                .orElseThrow(() -> new BookingFormNotFoundException("No form exists for this event, create one"));

        bookingForm.getFields().clear();

        List<FormQuestion> newFields = request.getFormFields().stream()
                .map(q -> buildField(q, bookingForm))
                .collect(Collectors.toList());

        bookingForm.setFields(newFields);
        return bookingFormRepository.save(bookingForm);
    }

    public BookingForm getForm(Long eventId) {
        return bookingFormRepository.findByEventId(eventId)
                .orElseThrow(() -> new BookingFormNotFoundException("Booking form not found"));
    }

    @Transactional
    public List<FormAnswer> submitAnswers(Slot slot, SubmitFormAnswers request) {
        bookingFormValidator.validateAnswers(slot, slot.getEvent().getBookingForm().getFields(), request.getAnswers());

        List<FormAnswer> answers = request.getAnswers().stream()
                .map(q -> buildAnswer(q, slot))
                .collect(Collectors.toList());

        return answers;
    }

    public List<FormAnswer> getAnswers(Long slotId) {
        return formAnswerRepository.findBySlotId(slotId);
    }

    private FormQuestion buildField(FormQuestionDTO dto, BookingForm form) {
        return FormQuestion.builder()
                .bookingForm(form)
                .label(dto.getLabel())
                .fieldType(dto.getFieldType())
                .required(dto.isRequired())
                .displayOrder(dto.getDisplayOrder())
                .build();
    }

    private FormAnswer buildAnswer(FieldAnswerDTO dto, Slot slot) {
        FormAnswer formAnswer = FormAnswer.builder()
                .slot(slot)
                .formField(findQuestionById(dto.getFieldId()))
                .answer(dto.getFieldResponse())
                .build();

        return formAnswerRepository.save(formAnswer);
    }

    private FormQuestion findQuestionById(UUID questionId) {
        return formQuestionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException("Field not found"));
    }

    private void validateHost(Event event) {
        if (!event.getHost().getId().equals(userService.getCurrentUser().getId())) {
            throw new UnauthorizedAccessException("You are not authorized to access other user's event");
        }
    }
}
