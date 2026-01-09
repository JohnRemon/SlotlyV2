package com.example.SlotlyV2.common.util;

import org.springframework.stereotype.Component;

import com.example.SlotlyV2.feature.event.Event;
import com.example.SlotlyV2.feature.slot.Slot;
import com.example.SlotlyV2.feature.user.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NameUtils {

    public String getUserDisplayName(User user) {
        String displayName = "";
        if (user.getFirstName() != null) {
            displayName += user.getFirstName();
        }
        if (user.getLastName() != null) {
            displayName += " " + user.getLastName();
        }

        return displayName.trim();
    }

    public String getUserDisplayName(Event event) {
        return getUserDisplayName(event.getHost());
    }

    public String getUserDisplayName(Slot slot) {
        return getUserDisplayName(slot.getEvent().getHost());
    }
}
