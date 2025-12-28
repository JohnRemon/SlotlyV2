package com.example.SlotlyV2.exception;

public class TokenAlreadyExpiredException extends RuntimeException {
    public TokenAlreadyExpiredException(String message) {
        super(message);
    }
}
