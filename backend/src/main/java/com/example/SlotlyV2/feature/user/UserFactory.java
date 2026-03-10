package com.example.SlotlyV2.feature.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.SlotlyV2.feature.auth.enums.AuthProvider;
import com.example.SlotlyV2.feature.user.dto.RegisterRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserFactory {
    private final PasswordEncoder passwordEncoder;

    public User createFrom(RegisterRequest request) {
        return createFrom(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getFirstName(),
                request.getLastName(),
                request.getTimeZone(),
                false,
                null,
                AuthProvider.LOCAL);
    }

    public User createFrom(String email, String googleId, String firstName, String lastName) {
        return createFrom(
                email,
                null,
                firstName,
                lastName,
                "UTC",
                true,
                googleId,
                AuthProvider.GOOGLE);
    }

    private User createFrom(
            String email, String password, String firstName, String lastName, String timeZone, Boolean isVerified,
            String googleId, AuthProvider authProvider) {
        return User.builder()
                .email(email)
                .password(password)
                .firstName(firstName)
                .lastName(lastName)
                .timeZone(timeZone)
                .authProvider(authProvider)
                .googleId(googleId)
                .isVerified(isVerified)
                .build();

    }
}
