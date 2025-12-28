package com.example.SlotlyV2.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.SlotlyV2.dto.LoginRequest;
import com.example.SlotlyV2.dto.RegisterRequest;
import com.example.SlotlyV2.dto.UserRegistrationVerificationData;
import com.example.SlotlyV2.event.EmailVerificationEvent;
import com.example.SlotlyV2.exception.InvalidCredentialsException;
import com.example.SlotlyV2.exception.UnauthorizedAccessException;
import com.example.SlotlyV2.exception.UserAlreadyExistsException;
import com.example.SlotlyV2.exception.UsernameAlreadyExistsException;
import com.example.SlotlyV2.model.User;
import com.example.SlotlyV2.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final ApplicationEventPublisher eventPublisher;
    private final VerificationTokenService verificationTokenService;

    public User registerUser(RegisterRequest request) {
        // Check if email already exsists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User Already Exists. Please Login");
        }

        // Check if username already exists
        if (userRepository.existsByDisplayName(request.getDisplayName())) {
            throw new UsernameAlreadyExistsException("Username Already Exists. Please Choose Another One");
        }

        // Create the user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setDisplayName(request.getDisplayName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setTimeZone(request.getTimeZone());
        user.setIsVerified(false);

        // generate verification token
        String token = verificationTokenService.generateVerificationToken(user);

        // generate the needed verification data
        UserRegistrationVerificationData data = new UserRegistrationVerificationData(
                user.getDisplayName(),
                user.getEmail(),
                token);

        // Publish Verification Email Event
        eventPublisher.publishEvent(new EmailVerificationEvent(data));

        // Save and Return the user
        return user;
    }

    public User loginUser(LoginRequest request) {
        // Spring handles authentication and session creation
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            return (User) authentication.getPrincipal();
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid Credentials");
        }
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getPrincipal() == null) {
            throw new UnauthorizedAccessException("User not authenticated");
        }
        return (User) auth.getPrincipal();
    }

    public void logout(HttpServletRequest request) {
        if (getCurrentUser() == null) {
            throw new UnauthorizedAccessException("User not authenticated");
        }

        SecurityContextHolder.clearContext();

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
