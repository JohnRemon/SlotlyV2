package com.example.SlotlyV2.event;

import com.example.SlotlyV2.dto.PasswordResetDTO;

import lombok.Data;

@Data
public class PasswordResetEvent {
    private final PasswordResetDTO passwordResetDTO;
}
