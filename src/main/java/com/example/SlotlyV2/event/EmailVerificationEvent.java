package com.example.SlotlyV2.event;

import com.example.SlotlyV2.dto.UserVerificationDTO;

import lombok.Data;

@Data
public class EmailVerificationEvent {
    private final UserVerificationDTO userVerificationDTO;
}
