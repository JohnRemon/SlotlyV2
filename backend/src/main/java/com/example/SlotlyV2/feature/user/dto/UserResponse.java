package com.example.SlotlyV2.feature.user.dto;

import com.example.SlotlyV2.feature.user.User;

public class UserResponse {
    private final Long id;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String timeZone;
    private final boolean isVerified;

    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.timeZone = user.getTimeZone();
        this.isVerified = user.isVerified();
    }
}
