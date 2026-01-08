package com.example.SlotlyV2.common.exception.auth;

public class TokenAlreadyExpiredException extends RuntimeException {
    public TokenAlreadyExpiredException(String message) {
        super(message);
    }
}
