package com.example.SlotlyV2.feature.email.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
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
