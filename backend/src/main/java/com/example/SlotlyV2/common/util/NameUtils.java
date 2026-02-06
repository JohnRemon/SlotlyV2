package com.example.SlotlyV2.common.util;

import org.springframework.stereotype.Component;

import com.example.SlotlyV2.feature.booking.Booking;
import com.example.SlotlyV2.feature.event.Event;
import com.example.SlotlyV2.feature.slot.Slot;
import com.example.SlotlyV2.feature.user.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NameUtils {

    public String getUserFullName(User user) {
        String displayName = "";
        if (user.getFirstName() != null) {
            displayName += user.getFirstName();
        }
        if (user.getLastName() != null) {
            displayName += " " + user.getLastName();
        }

        return displayName.trim();
    }

    public String getUserFullName(Event event) {
        return getUserFullName(event.getHost());
    }

    public String getUserFullName(Slot slot) {
        return getUserFullName(slot.getEvent().getHost());
    }

    public String getUserFullName(Booking booking) {
        return getUserFullName(booking.getSlot().getEvent().getHost());
    }
}
