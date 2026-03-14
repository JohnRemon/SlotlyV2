package com.example.SlotlyV2.feature.event;

import org.springframework.stereotype.Component;

import com.example.SlotlyV2.common.exception.event.InvalidEventException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EventValidator {
    public void validateNewCapacity(Integer newCapacity, Integer bookedSlots) {
        if (newCapacity != null && newCapacity < bookedSlots) {
            throw new InvalidEventException(
                    "Cannot reduce capacity to " + newCapacity +
                            " — " + bookedSlots + " slots are already booked");
        }
    }
}
