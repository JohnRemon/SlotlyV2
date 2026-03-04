package com.example.SlotlyV2.feature.auth.oauth;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.SlotlyV2.common.config.GoogleConfig;
import com.example.SlotlyV2.common.config.JwtProperties;
import com.example.SlotlyV2.common.exception.auth.GoogleOAuth2Exception;
import com.example.SlotlyV2.common.security.JwtTokenProvider;
import com.example.SlotlyV2.feature.auth.dto.JwtAuthenticationResponse;
import com.example.SlotlyV2.feature.auth.enums.AuthProvider;
import com.example.SlotlyV2.feature.schedule.ScheduleService;
import com.example.SlotlyV2.feature.user.User;
import com.example.SlotlyV2.feature.user.UserRepository;
import com.example.SlotlyV2.feature.user.dto.UserResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleOAuthService {

    private final GoogleConfig config;
    private final NetHttpTransport httpTransport;
    private final JsonFactory jsonFactory;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final UserRepository userRepository;
    private final ScheduleService scheduleService;

    @Transactional
    public JwtAuthenticationResponse login(String idTokenString) {
        Payload payload = verifyGoogleToken(idTokenString);

        String email = payload.getEmail();
        String googleId = payload.getSubject();
        String firstName = (String) payload.get("given_name");
        String lastName = (String) payload.get("family_name");

        User user = processOAuthUser(email, googleId, firstName, lastName);

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);
        Long expiresIn = jwtProperties.getAccessTokenExpiration() / 1000;

        log.info("Google OAuth2 login successful for user: {} (id: {})", email, user.getId());

        return JwtAuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(expiresIn)
                .user(new UserResponse(user))
                .build();
    }

    private User processOAuthUser(String email, String googleId, String firstName, String lastName) {
        return userRepository.findByEmail(email)
                .map(existingUser -> updateExistingUser(existingUser, googleId))
                .orElseGet(() -> createNewGoogleUser(email, googleId, firstName, lastName));
    }

    private User createNewGoogleUser(String email, String googleId, String firstName, String lastName) {
        User user = User.builder()
                .email(email)
                .googleId(googleId)
                .firstName(firstName)
                .lastName(lastName)
                .authProvider(AuthProvider.GOOGLE)
                .timeZone("UTC")
                .isVerified(true)
                .build();

        user = userRepository.save(user);

        scheduleService.createDefaultSchedule(user);

        return user;
    }

    private User updateExistingUser(User user, String googleId) {
        user.setAuthProvider(AuthProvider.GOOGLE);
        if (user.getGoogleId() == null) {
            user.setGoogleId(googleId);
        }

        if (!user.isVerified()) {
            user.setVerified(true);
        }

        return userRepository.save(user);

    }

    private Payload verifyGoogleToken(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    httpTransport, jsonFactory)
                    .setAudience(Collections.singletonList(config.getClientId()))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken == null) {
                throw new GoogleOAuth2Exception("Invalid Google ID token");
            }

            Payload payload = idToken.getPayload();

            if (!Boolean.TRUE.equals(payload.getEmailVerified())) {
                throw new GoogleOAuth2Exception("Google account email is not verified");
            }

            return payload;

        } catch (GoogleOAuth2Exception | GeneralSecurityException | IOException e) {
            throw new GoogleOAuth2Exception("Failed to verify Google ID token", e);
        }
    }

}
