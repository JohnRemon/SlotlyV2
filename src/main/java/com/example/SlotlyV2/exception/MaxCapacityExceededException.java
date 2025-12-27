package com.example.SlotlyV2.exception;

public class MaxCapacityExceededException extends RuntimeException {
    public MaxCapacityExceededException(String message) {
        super(message);
    }
}
