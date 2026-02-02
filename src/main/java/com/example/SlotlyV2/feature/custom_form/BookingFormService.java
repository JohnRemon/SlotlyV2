package com.example.SlotlyV2.feature.custom_form;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.SlotlyV2.common.exception.event.EventNotFoundException;
import com.example.SlotlyV2.feature.custom_form.dto.BookingFormRequest;
import com.example.SlotlyV2.feature.custom_form.dto.FormFieldDTO;
import com.example.SlotlyV2.feature.event.Event;
import com.example.SlotlyV2.feature.event.EventRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingFormService {
    private final BookingFormRepository bookingFormRepository;
    private final EventRepository eventRepository;

    public BookingForm createBookingForm(BookingFormRequest dto) {
        Event event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        BookingForm bookingForm = BookingForm.builder()
                .event(event)
                .build();

        List<FormField> fields = dto.getFormFields().stream()
                .map(fieldDTO -> buildFormField(fieldDTO, bookingForm))
                .collect(Collectors.toList());

        bookingForm.setFields(fields);

        return bookingFormRepository.save(bookingForm);
    }

    private FormField buildFormField(FormFieldDTO fieldDTO, BookingForm bookingForm) {
        return FormField.builder()
                .bookingForm(bookingForm)
                .label(fieldDTO.getLabel())
                .fieldType(fieldDTO.getFieldType())
                .required(fieldDTO.isRequired())
                .displayOrder(fieldDTO.getDisplayOrder())
                .build();
    }

    public List<FormField> getBookingForm(BookingFormRequest request) {
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        return event.getBookingForm().getFields();
    }
}
