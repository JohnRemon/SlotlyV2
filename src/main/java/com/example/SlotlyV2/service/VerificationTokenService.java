package com.example.SlotlyV2.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.SlotlyV2.exception.AccountAlreadyVerifiedException;
import com.example.SlotlyV2.exception.InvalidTokenException;
import com.example.SlotlyV2.exception.TokenAlreadyExpiredException;
import com.example.SlotlyV2.model.User;
import com.example.SlotlyV2.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationTokenService {
    private final UserRepository userRepository;

    public String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();

        user.setVerificationToken(token);
        user.setVerificationTokenExpiresAt(LocalDateTime.now().plusHours(24));
        userRepository.save(user);

        return token;
    }

    public Boolean verifyVerificationToken(String token) {
        log.info("Verifying the token");

        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid token"));

        if (user.getIsVerified()) {
            throw new AccountAlreadyVerifiedException("Account already verified");
        }

        if (user.getVerificationTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenAlreadyExpiredException("Token has expired");
        }

        user.setIsVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiresAt(null);
        userRepository.save(user);

        return true;
    }
}
