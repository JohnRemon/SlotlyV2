package com.example.SlotlyV2.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CancelBookingRequest {
    private Long eventId;
    private LocalDateTime startTime;
}
