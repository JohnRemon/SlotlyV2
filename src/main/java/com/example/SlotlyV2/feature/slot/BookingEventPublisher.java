package com.example.SlotlyV2.feature.slot;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.SlotlyV2.common.util.NameUtils;
import com.example.SlotlyV2.feature.calendar.dto.CalendarSyncDataDTO;
import com.example.SlotlyV2.feature.calendar.events.SlotBookedSyncEvent;
import com.example.SlotlyV2.feature.calendar.events.SlotCancelledSyncEvent;
import com.example.SlotlyV2.feature.email.dto.BookingEmailDTO;
import com.example.SlotlyV2.feature.email.event.SlotBookedEvent;
import com.example.SlotlyV2.feature.email.event.SlotCancelledEvent;
import com.example.SlotlyV2.feature.slot.dto.SlotCancelledEmailDTO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookingEventPublisher {
    private final ApplicationEventPublisher eventPublisher;
    private final NameUtils nameUtils;

    public void publishBookingEvents(Slot slot) {
        String hostDisplayName = nameUtils.getUserDisplayName(slot);
        BookingEmailDTO bookingData = BookingEmailDTO.builder()
                .toEmail(slot.getBookedByEmail())
                .hostEmail(slot.getEvent().getHost().getEmail())
                .attendeeName(slot.getBookedByName())
                .attendeeEmail(slot.getBookedByEmail())
                .eventName(slot.getEvent().getEventName())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .timeZone(slot.getEvent().getTimeZone())
                .hostDisplayName(hostDisplayName)
                .slotId(slot.getId())
                .build();

        CalendarSyncDataDTO calendarSyncDataDTO = CalendarSyncDataDTO.builder()
                .userId(slot.getEvent().getHost().getId())
                .slotId(slot.getId())
                .build();

        eventPublisher.publishEvent(new SlotBookedEvent(bookingData));
        eventPublisher.publishEvent(new SlotBookedSyncEvent(calendarSyncDataDTO));
    }

    public void publishCancellationEvents(Slot slot, String attendeeName, String attendeeEmail) {
        String hostDisplayName = nameUtils.getUserDisplayName(slot);

        SlotCancelledEmailDTO cancellationData = new SlotCancelledEmailDTO(
                slot.getId(),
                slot.getStartTime().toString(),
                slot.getEndTime().toString(),
                attendeeName,
                attendeeEmail,
                slot.getEvent().getEventName(),
                hostDisplayName,
                slot.getEvent().getHost().getEmail());

        CalendarSyncDataDTO calendarSyncDataDTO = CalendarSyncDataDTO.builder()
                .userId(slot.getEvent().getHost().getId())
                .slotId(slot.getId())
                .build();

        eventPublisher.publishEvent(new SlotCancelledEvent(cancellationData));
        eventPublisher.publishEvent(new SlotCancelledSyncEvent(calendarSyncDataDTO));
    }
}
