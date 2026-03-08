package com.example.SlotlyV2.feature.calendar;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.SlotlyV2.common.dto.ApiResponse;
import com.example.SlotlyV2.common.exception.auth.InvalidTokenException;
import com.example.SlotlyV2.common.exception.auth.UnauthorizedAccessException;
import com.example.SlotlyV2.feature.calendar.dto.ConnectResponse;
import com.example.SlotlyV2.feature.calendar.dto.ConnectionStatus;
import com.example.SlotlyV2.feature.calendar.dto.ExchangeRequest;
import com.example.SlotlyV2.feature.user.User;
import com.example.SlotlyV2.feature.user.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/calendar/google")
@RequiredArgsConstructor
@Slf4j
public class GoogleCalendarController {

    private static final String OAUTH_STATE_KEY = "google_oauth_state";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final GoogleCalendarTokenService googleOAuth2Service;
    private final UserService userService;

    @GetMapping("/connect")
    public ApiResponse<ConnectResponse> initiateConnection(HttpServletRequest request) {
        User user = userService.getCurrentUser();

        // Generate secure random state for CSRF protection
        String state = generateSecureState();

        // Store state in session for validation
        HttpSession session = request.getSession(true);
        session.setAttribute(OAUTH_STATE_KEY, state);

        String authorizationUrl = googleOAuth2Service.generateCalendarAuthorizationUrl(state);

        log.info("Generated Google OAuth URL for user {}", user.getId());

        return new ApiResponse<>("Authorization URL generated. Redirect user to this URL.",
                new ConnectResponse(authorizationUrl));
    }

    @PostMapping("/exchange")
    public ApiResponse<Void> exchangeAuthorizationCode(
            @Valid @RequestBody ExchangeRequest request,
            HttpServletRequest httpRequest) {

        User user = userService.getCurrentUser();
        HttpSession session = httpRequest.getSession(false);

        // Validate session exists
        if (session == null) {
            log.warn("No session found for OAuth callback for user {}", user.getId());
            throw new UnauthorizedAccessException("Session expired. Please try connecting again.");
        }

        // Validate state (CSRF protection)
        String expectedState = (String) session.getAttribute(OAUTH_STATE_KEY);
        if (expectedState == null) {
            if (googleOAuth2Service.isConnected(user.getId())) {
                log.info("Google Calendar already connected for user {}", user.getId());
                return new ApiResponse<>("Google Calendar already connected", null);
            }
            log.warn("No OAuth state found in session for user {}", user.getId());
            throw new InvalidTokenException("OAuth state not found. Please restart the connection process.");
        }

        if (!expectedState.equals(request.getState())) {
            log.warn("OAuth state mismatch for user {}. Expected: {}, Got: {}",
                    user.getId(), expectedState, request.getState());
            session.removeAttribute(OAUTH_STATE_KEY);
            throw new InvalidTokenException("Invalid OAuth state. Possible CSRF attack detected.");
        }

        session.removeAttribute(OAUTH_STATE_KEY);

        // Exchange authorization code for tokens
        googleOAuth2Service.connectCalendar(request.getCode(), user);

        log.info("Successfully connected Google Calendar for user {}", user.getId());

        return new ApiResponse<>("Google Calendar connected successfully", null);
    }

    @DeleteMapping("/disconnect")
    public ApiResponse<Void> disconnect() {
        User user = userService.getCurrentUser();

        googleOAuth2Service.disconnect(user);

        log.info("Disconnected Google Calendar for user {}", user.getId());

        return new ApiResponse<>("Google Calendar disconnected successfully", null);
    }

    @GetMapping("/status")
    public ApiResponse<ConnectionStatus> getConnectionStatus() {
        User user = userService.getCurrentUser();

        boolean connected = googleOAuth2Service.isConnected(user.getId());

        return new ApiResponse<>("Connection status retrieved", new ConnectionStatus(connected));
    }

    private String generateSecureState() {
        return new BigInteger(130, SECURE_RANDOM).toString(32);
    }
}
