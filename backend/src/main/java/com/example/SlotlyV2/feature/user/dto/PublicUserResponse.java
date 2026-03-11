
package com.example.SlotlyV2.feature.user.dto;

import com.example.SlotlyV2.feature.user.User;

import lombok.Value;

@Value
public class PublicUserResponse {
    private final String firstName;
    private final String lastName;

    public PublicUserResponse(User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
    }
}
