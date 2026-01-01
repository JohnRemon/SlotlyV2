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

    public User generateEmailVerificationToken(User user) {
        String token = UUID.randomUUID().toString();

        // TODO: Hash Tokens Before Saving
        user.setEmailVerificationToken(token);
        user.setEmailVerificationTokenExpiresAt(LocalDateTime.now().plusHours(24));
        return userRepository.save(user);

    }

    public Boolean verifyVerificationToken(String token) {
        User user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid token"));

        if (user.getIsVerified()) {
            throw new AccountAlreadyVerifiedException("Account already verified");
        }

        if (user.getEmailVerificationTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenAlreadyExpiredException("Token has expired");
        }

        user.setIsVerified(true);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationTokenExpiresAt(null);
        userRepository.save(user);

        return true;
    }

    public User generatePasswordVerificationToken(User user) {
        String token = UUID.randomUUID().toString();

        user.setPasswordVerificationToken(token);
        user.setPasswordVerificationTokenExpiresAt(LocalDateTime.now().plusMinutes(30));

        return userRepository.save(user);
    }

    public User verifyPasswordVerificationToken(String token) {
        User user = userRepository.findByPasswordVerificationToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid Token"));

        if (user.getPasswordVerificationTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenAlreadyExpiredException("Token has expired");
        }

        return user;
    }
}
