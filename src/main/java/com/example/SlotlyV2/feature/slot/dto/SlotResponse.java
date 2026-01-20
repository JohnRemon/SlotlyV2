package com.example.SlotlyV2.feature.slot.dto;

import java.time.LocalDateTime;

import com.example.SlotlyV2.common.util.TimeZoneConverter;
import com.example.SlotlyV2.feature.event.dto.EventResponse;
import com.example.SlotlyV2.feature.slot.Slot;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class SlotResponse {
    private EventResponse eventResponse;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String bookedByName;
    private String bookedByEmail;
    private LocalDateTime bookedAt;

    public SlotResponse(Slot slot, String userTimezone, TimeZoneConverter timeZoneConverter) {
        this.eventResponse = new EventResponse(slot.getEvent(), userTimezone, timeZoneConverter);
        this.startTime = timeZoneConverter.toUserTimezone(slot.getStartTime(), userTimezone);
        this.endTime = timeZoneConverter.toUserTimezone(slot.getEndTime(), userTimezone);
        this.bookedByName = slot.getBookedByName();
        this.bookedByEmail = slot.getBookedByEmail();
        this.bookedAt = slot.getBookedAt() != null
                ? timeZoneConverter.toUserTimezone(slot.getBookedAt(), userTimezone)
                : null;
    }
}
