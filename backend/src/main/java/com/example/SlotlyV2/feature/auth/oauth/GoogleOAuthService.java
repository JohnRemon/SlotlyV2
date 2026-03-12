package com.example.SlotlyV2.feature.auth.oauth;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.SlotlyV2.common.config.GoogleConfig;
import com.example.SlotlyV2.common.exception.auth.GoogleOAuth2Exception;
import com.example.SlotlyV2.feature.auth.enums.AuthProvider;
import com.example.SlotlyV2.feature.schedule.ScheduleService;
import com.example.SlotlyV2.feature.user.User;
import com.example.SlotlyV2.feature.user.UserFactory;
import com.example.SlotlyV2.feature.user.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleOAuthService {
    private final GoogleConfig config;
    private final NetHttpTransport httpTransport;
    private final JsonFactory jsonFactory;
    private final UserRepository userRepository;
    private final UserFactory userFactory;
    private final SecurityContextRepository securityContextRepository;
    private final ScheduleService scheduleService;

    @Transactional
    public User login(String idTokenString, String timeZone, HttpServletRequest request,
            HttpServletResponse response) {

        Payload payload = verifyGoogleToken(idTokenString);

        String email = payload.getEmail();
        String googleId = payload.getSubject();
        String firstName = (String) payload.get("given_name");
        String lastName = (String) payload.get("family_name");

        User user = findOrCreateOAuthUser(email, googleId, firstName, lastName, timeZone);
        persistSession(user, request, response);

        return user;
    }

    // -- Private helpers -------------------------------------------------------

    private User findOrCreateOAuthUser(String email, String googleId, String firstName, String lastName,
            String timeZone) {
        return userRepository.findByEmail(email)
                .map(existingUser -> updateExistingUser(existingUser, googleId))
                .orElseGet(() -> createNewGoogleUser(email, googleId, firstName, lastName, timeZone));
    }

    private User createNewGoogleUser(String email, String googleId, String firstName, String lastName,
            String timeZone) {
        User user = userFactory.createFrom(email, googleId, firstName, lastName, timeZone);
        user = userRepository.save(user);
        scheduleService.createDefaultScheduleForUser(user);
        log.info("User registered using google userId={} email={}", user.getId(), user.getEmail());
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

    private void persistSession(User user, HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);
    }

    private Payload verifyGoogleToken(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(httpTransport, jsonFactory)
                    .setAudience(Collections.singletonList(config.getClientId()))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken == null) {
                throw new GoogleOAuth2Exception("Invalid Google ID token");
            }

            Payload payload = idToken.getPayload();

            if (!payload.getEmailVerified()) {
                throw new GoogleOAuth2Exception("Google account email is not verified");
            }

            return payload;

        } catch (GoogleOAuth2Exception | GeneralSecurityException | IOException e) {
            throw new GoogleOAuth2Exception("Failed to verify Google ID token: " + e.getMessage(), e);
        }
    }
}
