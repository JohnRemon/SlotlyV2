package com.example.SlotlyV2.common.exception.schedule;

public class BlockedPeriodNotFoundException extends RuntimeException {
    public BlockedPeriodNotFoundException(String message) {
        super(message);
    }
}
