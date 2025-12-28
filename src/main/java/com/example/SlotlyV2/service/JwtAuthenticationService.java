package com.example.SlotlyV2.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.SlotlyV2.config.JwtProperties;
import com.example.SlotlyV2.dto.JwtAuthenticationResponse;
import com.example.SlotlyV2.dto.JwtLoginRequest;
import com.example.SlotlyV2.dto.RefreshTokenRequest;
import com.example.SlotlyV2.dto.RefreshTokenResponse;
import com.example.SlotlyV2.dto.UserResponse;
import com.example.SlotlyV2.exception.AccountNotVerifiedException;
import com.example.SlotlyV2.exception.InvalidCredentialsException;
import com.example.SlotlyV2.model.User;
import com.example.SlotlyV2.repository.UserRepository;
import com.example.SlotlyV2.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtAuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final UserRepository userRepository;

    public JwtAuthenticationResponse login(JwtLoginRequest request) {
        // Check if user is verified
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid Credentials"));

        if (!user.getIsVerified()) {
            throw new AccountNotVerifiedException("Please verify your account first");
        }

        // Authenticate the user with email and password
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
