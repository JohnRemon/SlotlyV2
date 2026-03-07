package com.example.SlotlyV2.feature.event.dto;

import java.time.OffsetDateTime;

import com.example.SlotlyV2.common.util.TimeZoneConverter;
import com.example.SlotlyV2.feature.availability.dto.AvailabilityRulesDTO;
import com.example.SlotlyV2.feature.booking_form.dto.BookingFormFieldResponse;
import com.example.SlotlyV2.feature.booking_form.dto.BookingFormResponse;
import com.example.SlotlyV2.feature.event.Event;
import com.example.SlotlyV2.feature.user.dto.UserResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicEventResponse {
    private Long id;
    private String eventName;
    private String description;
    private OffsetDateTime eventStart;
    private OffsetDateTime eventEnd;
    private UserResponse host;
    private AvailabilityRulesDTO availabilityRulesDTO;
    private BookingFormResponse bookingForm;

    public PublicEventResponse(Event event, TimeZoneConverter timeZoneConverter) {
        this.id = event.getId();
        this.eventName = event.getEventName();
        this.description = event.getDescription();
        this.eventStart = timeZoneConverter.toUtc(event.getEventStart());
        this.eventEnd = timeZoneConverter.toUtc(event.getEventEnd());
        this.host = UserResponse.builder()
                .firstName(event.getHost().getFirstName())
                .lastName(event.getHost().getLastName())
                .build();
        this.availabilityRulesDTO = AvailabilityRulesDTO.builder()
                .slotDurationMinutes(event.getAvailabilityRules().getSlotDurationMinutes())
                .build();
        this.bookingForm = event.getBookingForm() != null
                ? BookingFormResponse.builder()
                        .fields(event.getBookingForm().getFields().stream()
                                .map(BookingFormFieldResponse::new)
                                .toList())
                        .build()
                : null;

    }
}
