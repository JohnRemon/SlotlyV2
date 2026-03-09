package com.example.SlotlyV2.feature.calendar;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.springframework.stereotype.Service;

import com.example.SlotlyV2.common.exception.auth.InvalidTokenException;
import com.example.SlotlyV2.common.exception.auth.UnauthorizedAccessException;
import com.example.SlotlyV2.feature.calendar.dto.ConnectionStatus;
import com.example.SlotlyV2.feature.user.User;
import com.example.SlotlyV2.feature.user.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleCalendarConnectionService {

    private static final String OAUTH_STATE_KEY = "google_oauth_state";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final GoogleCalendarTokenService tokenService;
    private final UserService userService;

    public String initiateConnection(HttpServletRequest request) {
        User currentUser = userService.getCurrentUser();
        String state = generateSecureState();

        HttpSession session = request.getSession(true);
        session.setAttribute(OAUTH_STATE_KEY, state);

        String authorizationUrl = tokenService.generateCalendarAuthorizationUrl(state);
        log.info("Google OAuth initiated userId={}", currentUser.getId());
        return authorizationUrl;
    }

    public void exchangeCode(String code, String state, HttpServletRequest request) {
        User currentUser = userService.getCurrentUser();
        HttpSession session = request.getSession(false);

        if (session == null) {
            log.warn("No session found for OAuth callback userId={}", currentUser.getId());
            throw new UnauthorizedAccessException("Session expired. Please try connecting again.");
        }

        String expectedState = (String) session.getAttribute(OAUTH_STATE_KEY);

        if (expectedState == null) {
            // Already connected via a previous exchange — idempotent success
            if (tokenService.isConnected(currentUser.getId())) {
                log.info("Google Calendar already connected userId={}", currentUser.getId());
                return;
            }
            log.warn("No OAuth state in session userId={}", currentUser.getId());
            throw new InvalidTokenException("OAuth state not found. Please restart the connection process.");
        }

        if (!expectedState.equals(state)) {
            log.warn("OAuth state mismatch userId={}", currentUser.getId());
            session.removeAttribute(OAUTH_STATE_KEY);
            throw new InvalidTokenException("Invalid OAuth state. Possible CSRF attack detected.");
        }

        // Consume state — single use
        session.removeAttribute(OAUTH_STATE_KEY);

        tokenService.connectCalendar(code, currentUser);
        log.info("Google Calendar connected userId={}", currentUser.getId());
    }

    public void disconnect() {
        User currentUser = userService.getCurrentUser();
        tokenService.disconnect(currentUser);
        log.info("Google Calendar disconnected userId={}", currentUser.getId());
    }

    public ConnectionStatus getConnectionStatus() {
        User currentUser = userService.getCurrentUser();
        return new ConnectionStatus(tokenService.isConnected(currentUser.getId()));
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private String generateSecureState() {
        return new BigInteger(130, SECURE_RANDOM).toString(32);
    }
}
