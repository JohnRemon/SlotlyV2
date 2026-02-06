package com.example.SlotlyV2.feature.password_reset;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.SlotlyV2.common.exception.auth.PasswordMismatchException;
import com.example.SlotlyV2.common.util.NameUtils;
import com.example.SlotlyV2.feature.auth.VerificationTokenService;
import com.example.SlotlyV2.feature.email.event.PasswordResetEvent;
import com.example.SlotlyV2.feature.user.User;
import com.example.SlotlyV2.feature.user.UserRepository;
import com.example.SlotlyV2.feature.user.dto.PasswordResetConfirmRequest;
import com.example.SlotlyV2.feature.user.dto.PasswordResetDTO;
import com.example.SlotlyV2.feature.user.dto.PasswordResetRequest;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {
    private final UserRepository userRepository;
    private final VerificationTokenService verificationTokenService;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final NameUtils nameUtils;

    public void resetPasswordRequest(PasswordResetRequest request) {
        // Find user by email (return null if not found)
        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        // return successfully
        if (user == null) {
            log.info("Password reset requested for non-existing email: {}", request.getEmail());
            return;
        }

        // generate password token
        String token = verificationTokenService.generatePasswordResetToken(user);

        // generate needed password reset data
        PasswordResetDTO data = new PasswordResetDTO(
                nameUtils.getUserFullName(user),
                user.getEmail(),
                token);

        // publish password reset event
        eventPublisher.publishEvent(new PasswordResetEvent(data));
    }

    @Transactional(rollbackOn = Exception.class)
    public void resetPassword(String token, PasswordResetConfirmRequest request) {
        User user = verificationTokenService.verifyPasswordResetToken(token);

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("Passwords don't match");
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        log.info("Password reset successfully for user: {}", user.getEmail());
    }
}
