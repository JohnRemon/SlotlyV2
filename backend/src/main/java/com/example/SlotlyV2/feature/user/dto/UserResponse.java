package com.example.SlotlyV2.feature.user.dto;

import com.example.SlotlyV2.feature.user.User;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class UserResponse {
    @JsonProperty(index = 0)
    private final Long id;

    @JsonProperty(index = 5)
    private final String email;

    @JsonProperty(index = 10)
    private final String firstName;

    @JsonProperty(index = 15)
    private final String lastName;

    @JsonProperty(index = 20)
    private final String timeZone;

    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.timeZone = user.getTimeZone();
    }
}
