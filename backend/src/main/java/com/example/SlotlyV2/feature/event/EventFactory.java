package com.example.SlotlyV2.feature.event;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.SlotlyV2.feature.availability.AvailabilityRules;
import com.example.SlotlyV2.feature.availability.dto.AvailabilityRulesDTO;
import com.example.SlotlyV2.feature.availability.dto.AvailabilityRulesUpdateRequest;
import com.example.SlotlyV2.feature.booking_form.BookingForm;
import com.example.SlotlyV2.feature.booking_form.FormQuestion;
import com.example.SlotlyV2.feature.booking_form.dto.BookingFormRequest;
import com.example.SlotlyV2.feature.booking_form.enums.FieldType;
import com.example.SlotlyV2.feature.event.dto.EventRequest;
import com.example.SlotlyV2.feature.schedule.Schedule;
import com.example.SlotlyV2.feature.user.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EventFactory {
    public Event createFrom(EventRequest request, Schedule schedule, User user) {
        AvailabilityRules availabilityRules = buildAvailabilityRules(
                request.getAvailabilityRules());

        Event event = Event.builder()
                .eventName(request.getEventName())
                .description(request.getDescription())
                .host(user)
                .schedule(schedule)
                .availabilityRules(availabilityRules)
                .build();
        event.setBookingForm(buildBookingForm(request.getBookingForm(), event));
        return event;
    }

    public AvailabilityRules buildAvailabilityRules(AvailabilityRulesDTO dto) {
        return buildAvailabilityRules(
                dto.getSlotDurationMinutes(),
                dto.getMaxSlotsPerUser(),
                dto.getBufferMinutes(),
                dto.getMinimumNoticeHours(),
                dto.getMaximumAdvanceDays(),
                dto.getMaxCapacity(),
                dto.getAllowCancellations(),
                dto.getIsPublic());
    }

    public AvailabilityRules buildAvailabilityRules(AvailabilityRulesUpdateRequest request) {
        return buildAvailabilityRules(
                request.getSlotDurationMinutes(),
                request.getMaxSlotsPerUser(),
                request.getBufferMinutes(),
                request.getMinimumNoticeHours(),
                request.getMaximumAdvanceDays(),
                request.getMaxCapacity(),
                request.getAllowCancellations(),
                request.getIsPublic());
    }

    private AvailabilityRules buildAvailabilityRules(
            Integer slotDuration, Integer maxSlotsPerUser, Integer buffer,
            Integer minNotice, Integer maxAdvance, Integer maxCapacity,
            Boolean allowCancellations, Boolean isPublic) {
        return AvailabilityRules.builder()
                .slotDurationMinutes(orDefault(slotDuration, 30))
                .maxSlotsPerUser(orDefault(maxSlotsPerUser, 1))
                .bufferMinutes(orDefault(buffer, 0))
                .minimumNoticeHours(orDefault(minNotice, 0))
                .maximumAdvanceDays(orDefault(maxAdvance, 90))
                .maxCapacity(maxCapacity)
                .allowsCancellations(orDefault(allowCancellations, true))
                .isPublic(orDefault(isPublic, true))
                .build();
    }

    private BookingForm buildBookingForm(BookingFormRequest request, Event event) {
        BookingForm form = BookingForm.builder()
                .event(event)
                .build();

        List<FormQuestion> fields = new ArrayList<>();
        if (request != null) {
            fields.stream()
                    .map(field -> FormQuestion.builder()
                            .bookingForm(form)
                            .label(field.getLabel())
                            .fieldType(orDefault(field.getFieldType(), FieldType.TEXT))
                            .required(field.isRequired())
                            .displayOrder(orDefault(field.getDisplayOrder(), 0))
                            .build())
                    .toList();
        }

        form.setFields(fields);
        return form;
    }

    private <T> T orDefault(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

}
