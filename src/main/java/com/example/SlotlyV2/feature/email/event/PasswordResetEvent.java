package com.example.SlotlyV2.feature.email.event;

import com.example.SlotlyV2.feature.user.dto.PasswordResetDTO;

import lombok.Data;

@Data
public class PasswordResetEvent {
    private final PasswordResetDTO passwordResetDTO;
}
