package com.example.SlotlyV2.feature.user;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.SlotlyV2.common.exception.auth.UnauthorizedAccessException;
import com.example.SlotlyV2.common.exception.user.UserAlreadyExistsException;
import com.example.SlotlyV2.common.util.NameUtils;
import com.example.SlotlyV2.feature.auth.VerificationTokenService;
import com.example.SlotlyV2.feature.email.event.EmailVerificationEvent;
import com.example.SlotlyV2.feature.schedule.ScheduleService;
import com.example.SlotlyV2.feature.user.dto.RegisterRequest;
import com.example.SlotlyV2.feature.user.dto.UserEmailVerificationDTO;
import com.example.SlotlyV2.feature.user.dto.UserResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserFactory userFactory;
    private final ApplicationEventPublisher eventPublisher;
    private final VerificationTokenService verificationTokenService;
    private final ScheduleService scheduleService;
    private final NameUtils nameUtils;

    @Transactional
    public UserResponse registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("An account with this email already exists");
        }

        User user = userFactory.createFrom(request);
        userRepository.save(user);
        scheduleService.createDefaultScheduleForUser(user);

        publishVerificationEmail(user);

        log.info("User registered userId={} email={}", user.getId(), user.getEmail());
        return new UserResponse(user);
    }

    @Transactional
    public void verifyEmail(String token) {
        verificationTokenService.verifyEmailVerificationToken(token);
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof User)) {
            throw new UnauthorizedAccessException("User not authenticated");
        }
        return (User) auth.getPrincipal();
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void publishVerificationEmail(User user) {
        String token = verificationTokenService.generateEmailVerificationToken(user);
        eventPublisher.publishEvent(new EmailVerificationEvent(
                new UserEmailVerificationDTO(
                        nameUtils.getUserFullName(user),
                        user.getEmail(),
                        token)));
    }
}
