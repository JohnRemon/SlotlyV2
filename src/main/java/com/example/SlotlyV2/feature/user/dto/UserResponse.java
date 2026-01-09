package com.example.SlotlyV2.feature.user.dto;

import com.example.SlotlyV2.feature.user.User;

import lombok.Value;

@Value
public class UserResponse {
    private final Long id;
    private final String email;
    private final String displayName;
    private final String firstName;
    private final String lastName;
    private final String timeZone;
    private final Boolean isVerified;

    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.displayName = user.getDisplayName();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.timeZone = user.getTimeZone();
        this.isVerified = user.getIsVerified();
    }
}
