package com.example.SlotlyV2.feature.email.event;

import com.example.SlotlyV2.feature.user.dto.UserEmailVerificationDTO;

import lombok.Data;

@Data
public class EmailVerificationEvent {
    private final UserEmailVerificationDTO userEmailVerificationDTO;
}
