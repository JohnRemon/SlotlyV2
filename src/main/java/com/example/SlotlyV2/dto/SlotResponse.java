package com.example.SlotlyV2.dto;

import java.time.LocalDateTime;

import com.example.SlotlyV2.model.Event;
import com.example.SlotlyV2.model.Slot;

import lombok.Data;

@Data
public class SlotResponse {
    private Event event;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String bookedBy;
    private LocalDateTime bookedAt;

    public SlotResponse(Slot slot) {
        this.event = slot.getEvent();
        this.startTime = slot.getStartTime();
        this.endTime = slot.getEndTime();
        this.bookedBy = slot.getBookedBy();
        this.bookedAt = slot.getBookedAt();
    }
}
