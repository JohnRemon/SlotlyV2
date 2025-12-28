package com.example.SlotlyV2.event;

import com.example.SlotlyV2.dto.UserRegistrationVerificationData;

import lombok.Data;

@Data
public class EmailVerificationEvent {
    private final UserRegistrationVerificationData userRegistrationVerificationData;
}
