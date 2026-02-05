package com.example.SlotlyV2.feature.email.dto;

import java.util.List;

import lombok.Value;

@Value
public class EventCancelledEmailDTO {
    private final Long eventId;
    private final String eventName;
    private final List<String> attendeeEmails;
}
