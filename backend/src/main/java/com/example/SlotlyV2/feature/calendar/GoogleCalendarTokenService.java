package com.example.SlotlyV2.feature.calendar;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.SlotlyV2.common.config.GoogleConfig;
import com.example.SlotlyV2.common.exception.calendar.GoogleCalendarException;
import com.example.SlotlyV2.common.exception.calendar.GoogleCalendarNotConnectedException;
import com.example.SlotlyV2.feature.user.User;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.calendar.CalendarScopes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleCalendarTokenService {

    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final Collection<String> SCOPES = List.of(CalendarScopes.CALENDAR);

    private final GoogleConfig config;
    private final NetHttpTransport httpTransport;
    private final JsonFactory jsonFactory;
    private final GoogleCalendarTokenRepository tokenRepository;

    public String generateCalendarAuthorizationUrl(String state) {
        return new GoogleAuthorizationCodeRequestUrl(
                config.getClientId(),
                config.getRedirectUri(),
                SCOPES)
                .setAccessType("offline")
                .set("prompt", "consent")
                .setState(state)
                .build();
    }

    @Transactional
    public void connectCalendar(String code, User user) {
        try {
            GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    httpTransport, jsonFactory, TOKEN_URL,
                    config.getClientId(), config.getClientSecret(),
                    code, config.getRedirectUri())
                    .execute();

            GoogleCalendarToken token = tokenRepository.findByUserId(user.getId())
                    .orElseGet(() -> GoogleCalendarToken.builder().user(user).build());

            token.setAccessToken(tokenResponse.getAccessToken());
            token.setExpiresAt(calculateExpirationTime(tokenResponse.getExpiresInSeconds()));
            token.setScope(tokenResponse.getScope());

            if (tokenResponse.getRefreshToken() != null) {
                token.setRefreshToken(tokenResponse.getRefreshToken());
            }

            tokenRepository.save(token);

        } catch (IOException e) {
            log.error("Failed to exchange authorization code userId={}", user.getId(), e);
            throw new GoogleCalendarException("Failed to connect to Google Calendar");
        }
    }

    public Credential getCredentials(Long userId) throws IOException {
        GoogleCalendarToken token = tokenRepository.findByUserId(userId)
                .orElseThrow(() -> new GoogleCalendarNotConnectedException(
                        "Please connect your Google Calendar"));

        if (token.isExpired()) {
            token = refreshAccessToken(token);
        }

        return buildCredential(token);
    }

    public boolean isConnected(Long userId) {
        return tokenRepository.existsByUserId(userId);
    }

    /**
     * Checks whether the user's Google Calendar is connected and the token is
     * usable. Unlike isConnected(), this also attempts a token refresh if expired.
     * Used before fire-and-forget sync operations where we want to skip gracefully
     * rather than throw.
     */
    public boolean isConnectedAndValid(Long userId) {
        return tokenRepository.findByUserId(userId)
                .map(token -> {
                    if (!token.isExpired())
                        return true;
                    if (token.getRefreshToken() == null)
                        return false;
                    try {
                        refreshAccessToken(token);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .orElse(false);
    }

    @Transactional
    public void disconnect(User user) {
        tokenRepository.deleteByUserId(user.getId());
    }

    // -- Private helpers -------------------------------------------------------

    @Transactional
    private GoogleCalendarToken refreshAccessToken(GoogleCalendarToken token) {
        if (token.getRefreshToken() == null) {
            throw new GoogleCalendarNotConnectedException(
                    "No refresh token available. Please reconnect your Google Calendar.");
        }

        try {
            GoogleTokenResponse tokenResponse = new GoogleRefreshTokenRequest(
                    httpTransport,
                    jsonFactory,
                    token.getRefreshToken(),
                    config.getClientId(),
                    config.getClientSecret())
                    .execute();

            token.setAccessToken(tokenResponse.getAccessToken());
            token.setExpiresAt(calculateExpirationTime(tokenResponse.getExpiresInSeconds()));

            if (tokenResponse.getRefreshToken() != null) {
                token.setRefreshToken(tokenResponse.getRefreshToken());
            }

            return tokenRepository.save(token);

        } catch (TokenResponseException e) {
            if (e.getDetails() != null && "invalid_grant".equals(e.getDetails().getError())) {
                log.warn("Refresh token revoked or expired userId={} — deleting stored token",
                        token.getUser().getId());
                tokenRepository.delete(token);
                throw new GoogleCalendarNotConnectedException(
                        "Your Google Calendar connection has expired. Please reconnect your account.");
            }

            log.error("Token refresh error userId={}", token.getUser().getId(), e);
            throw new GoogleCalendarException("Failed to refresh access token");

        } catch (IOException e) {
            log.error("Failed to refresh token userId={}", token.getUser().getId(), e);
            throw new GoogleCalendarException("Failed to refresh access token");
        }
    }

    private Credential buildCredential(GoogleCalendarToken token) {
        return new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                .setTransport(httpTransport)
                .setJsonFactory(jsonFactory)
                .setTokenServerEncodedUrl(TOKEN_URL)
                .setClientAuthentication(new ClientParametersAuthentication(
                        config.getClientId(),
                        config.getClientSecret()))
                .build()
                .setAccessToken(token.getAccessToken())
                .setRefreshToken(token.getRefreshToken())
                .setExpirationTimeMilliseconds(token.getExpiresAt().toInstant().toEpochMilli());
    }

    private OffsetDateTime calculateExpirationTime(Long expiresInSeconds) {
        if (expiresInSeconds == null) {
            log.warn("Google token response missing expiresInSeconds — defaulting to 1 hour");
            return OffsetDateTime.now().plusSeconds(3600);
        }
        return OffsetDateTime.now().plusSeconds(expiresInSeconds);
    }
}
