package com.example.SlotlyV2.feature.booking;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.SlotlyV2.common.util.NameUtils;
import com.example.SlotlyV2.feature.booking.dto.BookingCancelledEmailDTO;
import com.example.SlotlyV2.feature.calendar.dto.CalendarSyncDataDTO;
import com.example.SlotlyV2.feature.calendar.events.BookingSyncEvent;
import com.example.SlotlyV2.feature.calendar.events.SlotCancelledSyncEvent;
import com.example.SlotlyV2.feature.email.dto.BookingEmailDTO;
import com.example.SlotlyV2.feature.email.event.SlotBookedEvent;
import com.example.SlotlyV2.feature.email.event.SlotCancelledEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookingEventPublisher {
    private final ApplicationEventPublisher eventPublisher;
    private final NameUtils nameUtils;

    public void publishBookingEvents(Booking booking) {
        BookingEmailDTO bookingData = BookingEmailDTO.builder()
                .hostEmail(booking.getEvent().getHost().getEmail())
                .attendeeName(booking.getAttendeeDisplayName())
                .attendeeEmail(booking.getAttendeeEmail())
                .eventName(booking.getEvent().getEventName())
                .startTime(booking.getSlot().getStartTime())
                .endTime(booking.getSlot().getEndTime())
                .hostDisplayName(nameUtils.getUserFullName(booking))
                .build();

        CalendarSyncDataDTO calendarSyncDataDTO = CalendarSyncDataDTO.builder()
                .userId(booking.getEvent().getHost().getId())
                .bookingId(booking.getId())
                .build();

        eventPublisher.publishEvent(new SlotBookedEvent(bookingData));
        eventPublisher.publishEvent(new BookingSyncEvent(calendarSyncDataDTO));
    }

    public void publishCancellationEvents(Booking booking) {
        String hostDisplayName = nameUtils.getUserFullName(booking);

        BookingCancelledEmailDTO cancellationData = BookingCancelledEmailDTO.builder()
                .slotStartTime(booking.getSlot().getStartTime())
                .slotEndTime(booking.getSlot().getEndTime())
                .attendeeName(booking.getAttendeeName())
                .attendeeEmail(booking.getAttendeeEmail())
                .eventName(booking.getEvent().getEventName())
                .hostName(hostDisplayName)
                .hostEmail(booking.getEvent().getHost().getEmail())
                .build();

        CalendarSyncDataDTO calendarSyncDataDTO = CalendarSyncDataDTO.builder()
                .userId(booking.getEvent().getHost().getId())
                .bookingId(booking.getId())
                .build();

        eventPublisher.publishEvent(new SlotCancelledEvent(cancellationData));
        eventPublisher.publishEvent(new SlotCancelledSyncEvent(calendarSyncDataDTO));
    }
}
