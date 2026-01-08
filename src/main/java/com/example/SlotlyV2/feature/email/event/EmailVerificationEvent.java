package com.example.SlotlyV2.feature.email.event;

import com.example.SlotlyV2.feature.user.dto.UserVerificationDTO;

import lombok.Data;

@Data
public class EmailVerificationEvent {
    private final UserVerificationDTO userVerificationDTO;
}
