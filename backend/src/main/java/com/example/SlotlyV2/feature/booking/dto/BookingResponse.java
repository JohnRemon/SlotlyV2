package com.example.SlotlyV2.feature.booking.dto;

import java.time.OffsetDateTime;
import java.util.List;

import com.example.SlotlyV2.common.util.TimeZoneConverter;
import com.example.SlotlyV2.feature.booking.Booking;
import com.example.SlotlyV2.feature.booking.BookingStatus;
import com.example.SlotlyV2.feature.booking_form.dto.FieldResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BookingResponse {
    private final Long id;
    private final String attendeeName;
    private final String attendeeEmail;
    private final String eventName;
    private final OffsetDateTime startTime;
    private final OffsetDateTime endTime;
    private final BookingStatus bookingStatus;
    private final String notes;
    private final String cancellationReason;
    private final OffsetDateTime cancelledAt;
    private final List<FieldResponseDTO> formAnswers;

    public BookingResponse(Booking booking, String userTimezone, TimeZoneConverter timeZoneConverter) {
        this.id = booking.getId();
        this.attendeeName = booking.getAttendeeDisplayName();
        this.attendeeEmail = booking.getAttendeeEmail();
        this.eventName = booking.getEvent().getEventName();
        this.startTime = timeZoneConverter.toUserTimezone(booking.getSlot().getStartTime(), userTimezone);
        this.endTime = timeZoneConverter.toUserTimezone(booking.getSlot().getEndTime(), userTimezone);
        this.bookingStatus = booking.getStatus();
        this.notes = booking.getNotes();
        this.cancellationReason = booking.getCancellationReason();
        this.cancelledAt = booking.getCancelledAt();
        this.formAnswers = booking.getFormAnswers().stream()
                .map(FieldResponseDTO::new)
                .toList();
    }
}
