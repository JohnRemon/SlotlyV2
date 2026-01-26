package com.example.SlotlyV2.feature.calendar.events;

import com.example.SlotlyV2.feature.calendar.dto.CalendarSyncDataDTO;

import lombok.Data;

@Data
public class SlotBookedSyncEvent {
    private final CalendarSyncDataDTO calendarSyncDataDTO;
}
