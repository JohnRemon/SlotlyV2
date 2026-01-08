package com.example.SlotlyV2.feature.user.dto;

import com.example.SlotlyV2.feature.user.User;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String email;
    private String displayName;
    private String firstName;
    private String lastName;
    private String timeZone;
    private Boolean isVerified;

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
