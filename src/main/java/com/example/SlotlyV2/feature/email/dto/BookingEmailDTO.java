package com.example.SlotlyV2.feature.email.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BookingEmailDTO {
    private String toEmail;
    private String hostEmail;
    private String attendeeName;
    private String attendeeEmail;
    private String eventName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String timeZone;
    private String hostDisplayName;
    private Long slotId;
}
