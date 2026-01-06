package com.example.SlotlyV2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
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
