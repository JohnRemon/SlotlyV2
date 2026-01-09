package com.example.SlotlyV2.feature.slot.dto;

import lombok.Value;

@Value
public class SlotCancelledEmailDTO {
    private Long slotId;
    private String slotStartTime;
    private String slotEndTime;
    private String bookedByName;
    private String bookedByEmail;
    private String eventName;
    private String hostName;
    private String hostEmail;
}
