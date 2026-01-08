package com.example.SlotlyV2.feature.event.dto;

import java.time.LocalDateTime;

import com.example.SlotlyV2.feature.availability.AvailabilityRulesDTO;
import com.example.SlotlyV2.feature.event.Event;
import com.example.SlotlyV2.feature.user.dto.UserResponse;

import lombok.Data;

@Data
public class EventResponse {
    private Long id;
    private String eventName;
    private String description;
    private UserResponse host;
    private LocalDateTime eventStart;
    private LocalDateTime eventEnd;
    private String timeZone;
    private LocalDateTime createdAt;
    private AvailabilityRulesDTO rules;
    private String shareableId;

    public EventResponse(Event event) {
        this.id = event.getId();
        this.eventName = event.getEventName();
        this.description = event.getDescription();
        this.host = new UserResponse(event.getHost());
        this.eventStart = event.getEventStart();
        this.eventEnd = event.getEventEnd();
        this.timeZone = event.getTimeZone();
        this.createdAt = event.getCreatedAt();
        this.rules = new AvailabilityRulesDTO(event.getRules());
        this.shareableId = event.getShareableId();
    }

}
