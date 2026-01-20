package com.example.SlotlyV2.feature.email.dto;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingEmailDTO {
    private String toEmail;
    private String hostEmail;
    private String attendeeName;
    private String attendeeEmail;
    private String eventName;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private String timeZone;
    private String hostDisplayName;
    private Long slotId;
}
