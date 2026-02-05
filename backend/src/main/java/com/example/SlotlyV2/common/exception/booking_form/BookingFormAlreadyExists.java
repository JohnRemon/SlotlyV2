package com.example.SlotlyV2.common.exception.booking_form;

public class BookingFormAlreadyExists extends RuntimeException {
    public BookingFormAlreadyExists(String message) {
        super(message);
    }
}
