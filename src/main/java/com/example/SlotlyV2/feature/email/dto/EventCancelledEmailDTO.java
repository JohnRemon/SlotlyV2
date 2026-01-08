package com.example.SlotlyV2.feature.email.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventCancelledEmailDTO {
    private final Long eventId;
    private final String eventName;
    private final List<String> attendeeEmails;
}
