package com.example.SlotlyV2.feature.user;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final VerificationTokenService verificationTokenService;
    private final ScheduleService scheduleService;
    private final NameUtils nameUtils;

    @Transactional
    public User registerUser(RegisterRequest request) {
        validateUniqueEmailAndUsername(request);

        User user = buildAndSaveUser(request);

        scheduleService.createDefaultSchedule(user);

        sendVerificationEmail(user);

        return user;
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
    }

    private void validateEmailNotTaken(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("User already exists. Please login");
        }
    }

    private void sendVerificationEmail(User user) {
        String token = verificationTokenService.generateEmailVerificationToken(user);

        UserEmailVerificationDTO data = new UserEmailVerificationDTO(
                nameUtils.getUserFullName(user),
                user.getEmail(),
                token);

        eventPublisher.publishEvent(new EmailVerificationEvent(data));
    }

    private User buildAndSaveUser(RegisterRequest request) {
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .timeZone(request.getTimeZone())
                .isVerified(false)
                .build();

        return userRepository.save(user);
    }
}
