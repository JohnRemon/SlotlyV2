package com.example.SlotlyV2.feature.booking.dto;

import java.time.OffsetDateTime;

import com.example.SlotlyV2.common.util.TimeZoneConverter;
import com.example.SlotlyV2.feature.booking.Booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private String attendeeName;
    private String attendeeEmail;

    public BookingResponse(Booking booking, String userTimezone, TimeZoneConverter timeZoneConverter) {
        this.startTime = timeZoneConverter.toUserTimezone(booking.getSlot().getStartTime(), userTimezone);
        this.endTime = timeZoneConverter.toUserTimezone(booking.getSlot().getEndTime(), userTimezone);
        this.attendeeName = booking.getAttendeeDisplayName();
        this.attendeeEmail = booking.getAttendeeEmail();
    }
}
