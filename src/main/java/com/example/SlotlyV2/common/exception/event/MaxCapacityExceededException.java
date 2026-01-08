package com.example.SlotlyV2.common.exception.event;

public class MaxCapacityExceededException extends RuntimeException {
    public MaxCapacityExceededException(String message) {
        super(message);
    }
}
