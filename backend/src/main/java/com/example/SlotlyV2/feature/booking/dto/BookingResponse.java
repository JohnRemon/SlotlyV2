package com.example.SlotlyV2.feature.booking.dto;

import java.time.OffsetDateTime;
import java.util.List;

import com.example.SlotlyV2.common.util.TimeZoneConverter;
import com.example.SlotlyV2.feature.booking.Booking;
import com.example.SlotlyV2.feature.booking.BookingStatus;
import com.example.SlotlyV2.feature.booking_form.dto.BookingFormAnswerResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class BookingResponse {
    @JsonProperty(index = 0)
    private final Long id;

    @JsonProperty(index = 5)
    private final String attendeeName;

    @JsonProperty(index = 10)
    private final String attendeeEmail;

    @JsonProperty(index = 15)
    private final String eventName;

    @JsonProperty(index = 20)
    private final OffsetDateTime startTime;

    @JsonProperty(index = 25)
    private final OffsetDateTime endTime;

    @JsonProperty(index = 30)
    private final BookingStatus bookingStatus;

    @JsonProperty(index = 35)
    private final String notes;

    @JsonProperty(index = 40)
    private final String cancellationReason;

    @JsonProperty(index = 45)
    private final OffsetDateTime cancelledAt;

    @JsonProperty(index = 50)
    private final List<BookingFormAnswerResponse> formAnswers;

    public BookingResponse(Booking booking, TimeZoneConverter timeZoneConverter, String timeZone) {
        this.id = booking.getId();
        this.attendeeName = booking.getAttendeeDisplayName();
        this.attendeeEmail = booking.getAttendeeEmail();
        this.eventName = booking.getEvent().getEventName();
        this.startTime = timeZoneConverter.toTimezone(booking.getSlot().getStartTime(), timeZone);
        this.endTime = timeZoneConverter.toTimezone(booking.getSlot().getEndTime(), timeZone);
        this.bookingStatus = booking.getStatus();
        this.notes = booking.getNotes();
        this.cancellationReason = booking.getCancellationReason();
        this.cancelledAt = booking.getCancelledAt();
        this.formAnswers = booking.getFormAnswers().stream()
                .map(BookingFormAnswerResponse::new)
                .toList();
    }
}
