package com.example.SlotlyV2.feature.auth.verify_email;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.example.SlotlyV2.common.exception.user.UserNotFoundException;
import com.example.SlotlyV2.common.util.NameUtils;
import com.example.SlotlyV2.feature.auth.VerificationTokenService;
import com.example.SlotlyV2.feature.email.event.EmailVerificationEvent;
import com.example.SlotlyV2.feature.user.User;
import com.example.SlotlyV2.feature.user.UserRepository;
import com.example.SlotlyV2.feature.user.dto.UserEmailVerificationDTO;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VerifyEmailService {
    private final VerificationTokenService verificationTokenService;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final NameUtils nameUtils;

    @Transactional
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String token = verificationTokenService.generateEmailVerificationToken(user);
        eventPublisher.publishEvent(new EmailVerificationEvent(
                new UserEmailVerificationDTO(
                        nameUtils.getUserFullName(user),
                        user.getEmail(),
                        token)));
    }
}
