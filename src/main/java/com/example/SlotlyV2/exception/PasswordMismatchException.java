package com.example.SlotlyV2.exception;

public class PasswordMismatchException extends RuntimeException {
    public PasswordMismatchException(String message) {
        super(message);
    }
}
