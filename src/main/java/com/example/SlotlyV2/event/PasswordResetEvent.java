package com.example.SlotlyV2.event;

import com.example.SlotlyV2.dto.PasswordResetData;

import lombok.Data;

@Data
public class PasswordResetEvent {
    private final PasswordResetData passwordResetData;
}
