package com.example.SlotlyV2.feature.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarSyncDataDTO {
    private Long userId;
    private Long bookingId;
}
