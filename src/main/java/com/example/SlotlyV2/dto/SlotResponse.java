package com.example.SlotlyV2.dto;

import java.time.LocalDateTime;

import com.example.SlotlyV2.model.Slot;

import lombok.Data;

@Data
public class SlotResponse {
    private EventResponse eventResponse;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String bookedByName;
    private String bookedByEmail;
    private LocalDateTime bookedAt;

    public SlotResponse(Slot slot) {
        this.eventResponse = new EventResponse(slot.getEvent());
        this.startTime = slot.getStartTime();
        this.endTime = slot.getEndTime();
        this.bookedByName = slot.getBookedByName();
        this.bookedByEmail = slot.getBookedByEmail();
        this.bookedAt = slot.getBookedAt();
    }
}
