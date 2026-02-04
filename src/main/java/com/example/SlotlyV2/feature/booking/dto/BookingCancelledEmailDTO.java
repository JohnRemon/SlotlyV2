package com.example.SlotlyV2.feature.booking.dto;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingCancelledEmailDTO {
    private OffsetDateTime slotStartTime;
    private OffsetDateTime slotEndTime;
    private String attendeeName;
    private String attendeeEmail;
    private String eventName;
    private String hostName;
    private String hostEmail;
}
