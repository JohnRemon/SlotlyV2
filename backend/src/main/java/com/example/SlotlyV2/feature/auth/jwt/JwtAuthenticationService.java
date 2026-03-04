package com.example.SlotlyV2.feature.auth.jwt;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.SlotlyV2.common.config.JwtProperties;
import com.example.SlotlyV2.common.exception.auth.AccountNotVerifiedException;
import com.example.SlotlyV2.common.exception.auth.InvalidCredentialsException;
import com.example.SlotlyV2.common.security.JwtTokenProvider;
import com.example.SlotlyV2.feature.auth.dto.JwtAuthenticationResponse;
import com.example.SlotlyV2.feature.auth.dto.JwtLoginRequest;
import com.example.SlotlyV2.feature.auth.dto.RefreshTokenRequest;
import com.example.SlotlyV2.feature.auth.dto.RefreshTokenResponse;
import com.example.SlotlyV2.feature.user.User;
import com.example.SlotlyV2.feature.user.UserRepository;
import com.example.SlotlyV2.feature.user.dto.UserResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final UserRepository userRepository;

    public JwtAuthenticationResponse login(JwtLoginRequest request) {
        // Check if user is verified
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid Credentials"));

        // if (user.isOAuthUser()) {
        // throw new InvalidCredentialsException(
        // "This account uses Google sign-in. Please log in with Google.");
        // }

        if (!user.isVerified()) {
            throw new AccountNotVerifiedException("Please verify your account first");
        }

        // Authenticate the user with email and password
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword()));

            // Get the user
            user = (User) authentication.getPrincipal();

            // Generate Access and Refresh Tokens
            String accessToken = jwtTokenProvider.generateAccessToken(user);
            String refreshToken = jwtTokenProvider.generateRefreshToken(user);

            // Convert expiration to seconds
            Long expiresIn = jwtProperties.getAccessTokenExpiration() / 1000;

            return JwtAuthenticationResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .expiresIn(expiresIn)
                    .user(new UserResponse(user))
                    .build();

        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException("Invalid Credentials");
        }
    }

    public RefreshTokenResponse refresh(RefreshTokenRequest request) {
        // Get the refresh token from the request
        String refreshToken = request.getRefreshToken();

        // Validate the refresh token
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new InvalidCredentialsException("Invalid refresh token");
        }

        // Validate the token type
        String tokenType = jwtTokenProvider.getTokenType(refreshToken);
        if (!tokenType.equals("refresh")) {
            throw new InvalidCredentialsException("Token is not a refresh token");
        }

        // Get user email
        String email = jwtTokenProvider.getEmailFromToken(refreshToken);

        // Load the user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        // Generate new access token
        String newAccessToken = jwtTokenProvider.generateAccessToken(user);

        // Convert expiration to seconds
        Long expiresIn = jwtProperties.getAccessTokenExpiration() / 1000;

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .expiresIn(expiresIn)
                .build();
    }

}
