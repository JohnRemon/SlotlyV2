package com.example.SlotlyV2.common.exception.auth;

public class GoogleOAuth2Exception extends RuntimeException {
    public GoogleOAuth2Exception(String message, Throwable e) {
        super(message, e);
    }

    public GoogleOAuth2Exception(String message) {
        super(message);
    }
}
