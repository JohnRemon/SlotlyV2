package com.example.SlotlyV2.exception;

public class SlotNotBookedException extends RuntimeException {
    public SlotNotBookedException(String message) {
        super(message);
    }
}
