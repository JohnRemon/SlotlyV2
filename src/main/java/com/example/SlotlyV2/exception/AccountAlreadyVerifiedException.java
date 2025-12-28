package com.example.SlotlyV2.exception;

public class AccountAlreadyVerifiedException extends RuntimeException {
    public AccountAlreadyVerifiedException(String message) {
        super(message);
    }
}
