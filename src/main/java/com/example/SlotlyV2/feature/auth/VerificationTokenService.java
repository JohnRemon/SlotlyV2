package com.example.SlotlyV2.feature.auth;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.SlotlyV2.common.exception.auth.AccountAlreadyVerifiedException;
import com.example.SlotlyV2.common.exception.auth.InvalidTokenException;
import com.example.SlotlyV2.common.exception.auth.TokenAlreadyExpiredException;
import com.example.SlotlyV2.feature.user.User;
import com.example.SlotlyV2.feature.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationTokenService {
    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String generateEmailVerificationToken(User user) {
        invalidateExistingTokens(user.getId(), TokenType.EMAIL_VERIFICATION);

        String rawToken = UUID.randomUUID().toString();
        String hashedToken = passwordEncoder.encode(rawToken);

        VerificationToken token = VerificationToken.builder()
                .user(user)
                .tokenType(TokenType.EMAIL_VERIFICATION)
                .tokenHash(hashedToken)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .build();

        verificationTokenRepository.save(token);
        log.info("Generated email verification token for user: {}", user.getEmail());

        return rawToken;
    }

    @Transactional
    public void verifyEmailVerificationToken(String rawToken) {
        // Find all unused email verification tokens
        List<VerificationToken> tokens = verificationTokenRepository
                .findAllByTokenTypeAndUsedAtIsNull(TokenType.EMAIL_VERIFICATION);

        // Find the matching token by comparing hashes
        VerificationToken token = tokens.stream()
                .filter(t -> passwordEncoder.matches(rawToken, t.getTokenHash()))
                .findFirst()
                .orElseThrow(() -> new InvalidTokenException("Invalid Token"));

        if (token.getUser().isVerified()) {
            throw new AccountAlreadyVerifiedException("Account Already Verified");
        }

        if (token.isExpired()) {
            throw new TokenAlreadyExpiredException("Token Expired");
        }

        if (!token.isValid()) {
            throw new InvalidTokenException("Invalid Token");
        }

        User user = token.getUser();
        user.setVerified(true);
        token.markAsUsed();

        verificationTokenRepository.save(token);
        userRepository.save(user);

        log.info("Email verified for user: {}", user.getEmail());
    }

    @Transactional
    public String generatePasswordResetToken(User user) {
        invalidateExistingTokens(user.getId(), TokenType.PASSWORD_RESET);

        String rawToken = UUID.randomUUID().toString();
        String hashedToken = passwordEncoder.encode(rawToken);

        VerificationToken token = VerificationToken.builder()
                .user(user)
                .tokenType(TokenType.PASSWORD_RESET)
                .tokenHash(hashedToken)
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .build();

        verificationTokenRepository.save(token);
        log.info("Generated password verification token for user: {}", user.getEmail());

        return rawToken;
    }

    @Transactional
    public User verifyPasswordResetToken(String rawToken) {
        // Find all unused password reset tokens
        List<VerificationToken> tokens = verificationTokenRepository
                .findAllByTokenTypeAndUsedAtIsNull(TokenType.PASSWORD_RESET);

        // Find the matching token by comparing hashes
        VerificationToken token = tokens.stream()
                .filter(t -> passwordEncoder.matches(rawToken, t.getTokenHash()))
                .findFirst()
                .orElseThrow(() -> new InvalidTokenException("Invalid Token"));

        if (token.isExpired()) {
            throw new TokenAlreadyExpiredException("Token Expired");
        }

        if (!token.isValid()) {
            throw new InvalidTokenException("Invalid Token");
        }

        User user = token.getUser();
        token.markAsUsed();
        verificationTokenRepository.save(token);

        log.info("Password reset token verified for user: {}", user.getEmail());
        return user;
    }

    @Transactional
    public void invalidateUserToken(Long userId, TokenType tokenType) {
        invalidateExistingTokens(userId, tokenType);
    }

    @Transactional
    public void deleteExpiredTokens() {
        int deleted = verificationTokenRepository.deleteAllExpiredTokens(LocalDateTime.now());
        log.info("Deleted {} expired tokens", deleted);
    }

    private void invalidateExistingTokens(Long userId, TokenType tokenType) {
        verificationTokenRepository.invalidateAllUserTokens(userId, tokenType, LocalDateTime.now());
    }
}
