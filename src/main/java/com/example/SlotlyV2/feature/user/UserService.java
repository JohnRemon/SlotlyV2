package com.example.SlotlyV2.feature.user;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.SlotlyV2.common.exception.auth.AccountNotVerifiedException;
import com.example.SlotlyV2.common.exception.auth.InvalidCredentialsException;
import com.example.SlotlyV2.common.exception.auth.UnauthorizedAccessException;
import com.example.SlotlyV2.common.exception.user.UserAlreadyExistsException;
import com.example.SlotlyV2.common.exception.user.UsernameAlreadyExistsException;
import com.example.SlotlyV2.feature.auth.VerificationTokenService;
import com.example.SlotlyV2.feature.email.event.EmailVerificationEvent;
import com.example.SlotlyV2.feature.schedule.ScheduleService;
import com.example.SlotlyV2.feature.user.dto.LoginRequest;
import com.example.SlotlyV2.feature.user.dto.RegisterRequest;
import com.example.SlotlyV2.feature.user.dto.UserEmailVerificationDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final ApplicationEventPublisher eventPublisher;
    private final VerificationTokenService verificationTokenService;
    private final ScheduleService scheduleService;

    @Transactional(rollbackOn = Exception.class)
    public User registerUser(RegisterRequest request) {
        validateUniqueEmailAndUsername(request);

        User user = buildAndSaveUser(request);

        scheduleService.createDefaultSchedule(user);

        sendVerificationEmail(user);

        return user;
    }

    public User loginUser(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            User user = (User) authentication.getPrincipal();

            if (!user.isVerified()) {
                throw new AccountNotVerifiedException("Please verify your account first");
            }

            SecurityContextHolder.getContext().setAuthentication(authentication);

            return user;
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid Credentials");
        }
    }

    public void logout(HttpServletRequest request) {
        getCurrentUser();

        SecurityContextHolder.clearContext();

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
            throw new UnauthorizedAccessException("User not authenticated");
        }

        if (auth.getPrincipal() instanceof User) {
            return (User) auth.getPrincipal();
        }

        throw new UnauthorizedAccessException("User not authenticated");
    }

    private void validateUniqueEmailAndUsername(RegisterRequest request) {
        validateEmailNotTaken(request.getEmail());
        validateUsernameNotTaken(request.getDisplayName());
    }

    private void validateEmailNotTaken(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("User already exists. Please login");
        }
    }

    private void validateUsernameNotTaken(String displayName) {
        if (userRepository.existsByDisplayName(displayName)) {
            throw new UsernameAlreadyExistsException("Username already exists. Please choose another one");
        }
    }

    private void sendVerificationEmail(User user) {
        String token = verificationTokenService.generateEmailVerificationToken(user);

        UserEmailVerificationDTO data = new UserEmailVerificationDTO(
                user.getDisplayName(),
                user.getEmail(),
                token);

        eventPublisher.publishEvent(new EmailVerificationEvent(data));
    }

    private User buildAndSaveUser(RegisterRequest request) {
        User user = User.builder()
                .email(request.getEmail())
                .displayName(request.getDisplayName())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .timeZone(request.getTimeZone())
                .isVerified(false)
                .build();

        return userRepository.save(user);
    }
}
