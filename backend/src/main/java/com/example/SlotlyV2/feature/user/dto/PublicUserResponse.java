
package com.example.SlotlyV2.feature.user.dto;

import com.example.SlotlyV2.feature.user.User;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class PublicUserResponse {
    @JsonProperty(index = 0)
    private final String firstName;

    @JsonProperty(index = 5)
    private final String lastName;

    public PublicUserResponse(User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
    }
}
