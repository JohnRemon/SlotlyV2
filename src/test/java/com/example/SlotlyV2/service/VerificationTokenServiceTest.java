package com.example.SlotlyV2.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.SlotlyV2.common.exception.auth.InvalidTokenException;
import com.example.SlotlyV2.common.exception.auth.TokenAlreadyExpiredException;
import com.example.SlotlyV2.feature.auth.TokenType;
import com.example.SlotlyV2.feature.auth.VerificationToken;
import com.example.SlotlyV2.feature.auth.VerificationTokenRepository;
import com.example.SlotlyV2.feature.auth.VerificationTokenService;
import com.example.SlotlyV2.feature.user.User;
import com.example.SlotlyV2.feature.user.UserRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class VerificationTokenServiceTest {

    @Mock
    private VerificationTokenRepository tokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private VerificationTokenService verificationTokenService;

    private User testUser;
    private VerificationToken testToken;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .displayName("testuser")
                .password("encodedPassword")
                .isVerified(false)
                .build();

        testToken = VerificationToken.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .tokenHash("hashedToken")
                .tokenType(TokenType.EMAIL_VERIFICATION)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .usedAt(null)
                .build();

        doReturn(true).when(passwordEncoder).matches(anyString(), anyString());
    }

    // ============== generateEmailVerificationToken ==========

    @Test
    void shouldGenerateEmailVerificationTokenSuccessfully() {
        // Arrange
        when(passwordEncoder.encode(anyString())).thenReturn("hashedToken");
        when(tokenRepository.save(any(VerificationToken.class))).thenAnswer(i -> i.getArgument(0));
        when(tokenRepository.invalidateAllUserTokens(anyLong(), any(), any())).thenReturn(0);

        // Act
        String rawToken = verificationTokenService.generateEmailVerificationToken(testUser);

        // Assert
        assertNotNull(rawToken);
        assertTrue(rawToken.length() > 0);
        verify(tokenRepository).save(argThat(token -> token.getUser().equals(testUser) &&
                token.getTokenType() == TokenType.EMAIL_VERIFICATION));
    }

    @Test
    void shouldSetTokenExpirationTo24Hours() {
        // Arrange
        when(passwordEncoder.encode(anyString())).thenReturn("hashedToken");
        when(tokenRepository.save(any(VerificationToken.class))).thenAnswer(i -> i.getArgument(0));
        when(tokenRepository.invalidateAllUserTokens(anyLong(), any(), any())).thenReturn(0);

        // Act
        verificationTokenService.generateEmailVerificationToken(testUser);

        // Assert
        verify(tokenRepository).save(argThat(token -> token.getExpiresAt().isAfter(LocalDateTime.now()) &&
                token.getExpiresAt().isBefore(LocalDateTime.now().plusHours(25))));
    }

    @Test
    void shouldOverwriteExistingTokenWhenGeneratingNew() {
        // Arrange
        when(passwordEncoder.encode(anyString())).thenReturn("hashedToken");
        when(tokenRepository.save(any(VerificationToken.class))).thenAnswer(i -> i.getArgument(0));
        when(tokenRepository.invalidateAllUserTokens(anyLong(), any(), any())).thenReturn(0);

        // Act
        String rawToken1 = verificationTokenService.generateEmailVerificationToken(testUser);
        String rawToken2 = verificationTokenService.generateEmailVerificationToken(testUser);

        // Assert
        assertNotEquals(rawToken1, rawToken2);
        verify(tokenRepository, times(2)).save(any(VerificationToken.class));
        verify(tokenRepository, times(2)).invalidateAllUserTokens(anyLong(), any(), any());
    }

    @Test
    void shouldGenerateUniqueUUIDTokens() {
        // Arrange
        User user1 = User.builder().id(1L).email("user1@example.com").build();
        User user2 = User.builder().id(2L).email("user2@example.com").build();
        when(passwordEncoder.encode(anyString())).thenReturn("hashedToken");
        when(tokenRepository.save(any(VerificationToken.class))).thenAnswer(i -> i.getArgument(0));
        when(tokenRepository.invalidateAllUserTokens(anyLong(), any(), any())).thenReturn(0);

        // Act
        String rawToken1 = verificationTokenService.generateEmailVerificationToken(user1);
        String rawToken2 = verificationTokenService.generateEmailVerificationToken(user2);

        // Assert
        assertNotEquals(rawToken1, rawToken2);
    }

    // =================== verifyEmailVerificationToken ===================

    @Test
    void shouldVerifyEmailWithValidToken() {
        // Arrange
        String rawToken = "test-raw-token";

        // Mock repository to return list of tokens
        when(tokenRepository.findAllByTokenTypeAndUsedAtIsNull(TokenType.EMAIL_VERIFICATION))
                .thenReturn(Arrays.asList(testToken));

        // Mock password encoder to match the raw token with the stored hash
        when(passwordEncoder.matches(rawToken, testToken.getTokenHash())).thenReturn(true);
        when(tokenRepository.save(any(VerificationToken.class))).thenAnswer(i -> i.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        verificationTokenService.verifyEmailVerificationToken(rawToken);

        // Assert
        assertTrue(testUser.isVerified());
        assertNotNull(testToken.getUsedAt());
        verify(tokenRepository).save(testToken);
        verify(userRepository).save(testUser);
    }

    @Test
    void shouldThrowInvalidTokenExceptionWhenTokenNotFound() {
        // Arrange
        String rawToken = "invalid-token";

        // Return empty list
        when(tokenRepository.findAllByTokenTypeAndUsedAtIsNull(TokenType.EMAIL_VERIFICATION))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(InvalidTokenException.class,
                () -> verificationTokenService.verifyEmailVerificationToken(rawToken));
    }

    @Test
    void shouldThrowInvalidTokenExceptionWhenPasswordDoesNotMatch() {
        // Arrange
        String rawToken = "wrong-token";

        when(tokenRepository.findAllByTokenTypeAndUsedAtIsNull(TokenType.EMAIL_VERIFICATION))
                .thenReturn(Arrays.asList(testToken));

        // Mock password encoder to NOT match
        when(passwordEncoder.matches(rawToken, testToken.getTokenHash())).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidTokenException.class,
                () -> verificationTokenService.verifyEmailVerificationToken(rawToken));
    }

    @Test
    void shouldThrowInvalidTokenExceptionWhenTokenExpired() {
        // Arrange
        String rawToken = "expired-token";
        testToken.setExpiresAt(LocalDateTime.now().minusHours(1));

        when(tokenRepository.findAllByTokenTypeAndUsedAtIsNull(TokenType.EMAIL_VERIFICATION))
                .thenReturn(Arrays.asList(testToken));
        when(passwordEncoder.matches(rawToken, testToken.getTokenHash())).thenReturn(true);

        // Act & Assert
        assertThrows(TokenAlreadyExpiredException.class,
                () -> verificationTokenService.verifyEmailVerificationToken(rawToken));
    }

    @Test
    void shouldThrowInvalidTokenExceptionWhenTokenAlreadyUsed() {
        // Arrange
        String rawToken = "used-token";
        testToken.setUsedAt(LocalDateTime.now());

        // Token with usedAt set should not be returned by the query
        when(tokenRepository.findAllByTokenTypeAndUsedAtIsNull(TokenType.EMAIL_VERIFICATION))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(InvalidTokenException.class,
                () -> verificationTokenService.verifyEmailVerificationToken(rawToken));
    }

    @Test
    void shouldFindCorrectTokenWhenMultipleTokensExist() {
        // Arrange
        String rawToken = "correct-token";

        VerificationToken token1 = VerificationToken.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .tokenHash("hash1")
                .tokenType(TokenType.EMAIL_VERIFICATION)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .build();

        VerificationToken token2 = VerificationToken.builder()
                .id(UUID.randomUUID())
                .user(testUser)
                .tokenHash("hash2")
                .tokenType(TokenType.EMAIL_VERIFICATION)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .build();

        List<VerificationToken> tokens = Arrays.asList(token1, token2);

        when(tokenRepository.findAllByTokenTypeAndUsedAtIsNull(TokenType.EMAIL_VERIFICATION))
                .thenReturn(tokens);
        when(passwordEncoder.matches(rawToken, "hash1")).thenReturn(false);
        when(passwordEncoder.matches(rawToken, "hash2")).thenReturn(true);
        when(tokenRepository.save(any(VerificationToken.class))).thenAnswer(i -> i.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        verificationTokenService.verifyEmailVerificationToken(rawToken);

        // Assert
        assertTrue(testUser.isVerified());
        assertNotNull(token2.getUsedAt());
    }

    // =================== generatePasswordVerificationToken ===================

    @Test
    void shouldGeneratePasswordVerificationTokenSuccessfully() {
        // Arrange
        when(passwordEncoder.encode(anyString())).thenReturn("hashedToken");
        when(tokenRepository.save(any(VerificationToken.class))).thenAnswer(i -> i.getArgument(0));
        when(tokenRepository.invalidateAllUserTokens(anyLong(), any(), any())).thenReturn(0);

        // Act
        String rawToken = verificationTokenService.generatePasswordResetToken(testUser);

        // Assert
        assertNotNull(rawToken);
        verify(tokenRepository).save(argThat(token -> token.getTokenType() == TokenType.PASSWORD_RESET));
    }

    @Test
    void shouldSetPasswordTokenExpirationTo30Minutes() {
        // Arrange
        when(passwordEncoder.encode(anyString())).thenReturn("hashedToken");
        when(tokenRepository.save(any(VerificationToken.class))).thenAnswer(i -> i.getArgument(0));
        when(tokenRepository.invalidateAllUserTokens(anyLong(), any(), any())).thenReturn(0);

        // Act
        verificationTokenService.generatePasswordResetToken(testUser);

        // Assert
        verify(tokenRepository).save(argThat(token -> token.getExpiresAt().isAfter(LocalDateTime.now()) &&
                token.getExpiresAt().isBefore(LocalDateTime.now().plusMinutes(31))));
    }

    @Test
    void shouldOverwriteExistingPasswordTokenWhenGeneratingNew() {
        // Arrange
        when(passwordEncoder.encode(anyString())).thenReturn("hashedToken");
        when(tokenRepository.save(any(VerificationToken.class))).thenAnswer(i -> i.getArgument(0));
        when(tokenRepository.invalidateAllUserTokens(anyLong(), any(), any())).thenReturn(0);

        // Act
        String rawToken1 = verificationTokenService.generatePasswordResetToken(testUser);
        String rawToken2 = verificationTokenService.generatePasswordResetToken(testUser);

        // Assert
        assertNotEquals(rawToken1, rawToken2);
        verify(tokenRepository, times(2)).invalidateAllUserTokens(anyLong(), any(), any());
    }

    @Test
    void shouldThrowInvalidTokenExceptionForInvalidPasswordToken() {
        // Arrange
        String rawToken = "invalid-password-token";

        when(tokenRepository.findAllByTokenTypeAndUsedAtIsNull(TokenType.PASSWORD_RESET))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(InvalidTokenException.class,
                () -> verificationTokenService.verifyPasswordResetToken(rawToken));
    }

    @Test
    void shouldThrowInvalidTokenExceptionForMismatchedPasswordToken() {
        // Arrange
        String rawToken = "wrong-password-token";
        testToken.setTokenType(TokenType.PASSWORD_RESET);
        testToken.setExpiresAt(LocalDateTime.now().plusMinutes(30));

        when(tokenRepository.findAllByTokenTypeAndUsedAtIsNull(TokenType.PASSWORD_RESET))
                .thenReturn(Arrays.asList(testToken));
        when(passwordEncoder.matches(rawToken, testToken.getTokenHash())).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidTokenException.class,
                () -> verificationTokenService.verifyPasswordResetToken(rawToken));
    }

    @Test
    void shouldVerifyPasswordVerificationTokenSuccessfully() {
        // Arrange
        String rawToken = "valid-password-token";
        testToken.setTokenType(TokenType.PASSWORD_RESET);
        testToken.setExpiresAt(LocalDateTime.now().plusMinutes(30));

        when(tokenRepository.findAllByTokenTypeAndUsedAtIsNull(TokenType.PASSWORD_RESET))
                .thenReturn(Arrays.asList(testToken));
        when(passwordEncoder.matches(rawToken, testToken.getTokenHash())).thenReturn(true);
        when(tokenRepository.save(any(VerificationToken.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        User result = verificationTokenService.verifyPasswordResetToken(rawToken);

        // Assert
        assertEquals(testUser, result);
        assertNotNull(testToken.getUsedAt());
    }

    @Test
    void shouldTokenAlreadyExpiredExceptionForExpiredPasswordToken() {
        // Arrange
        String rawToken = "expired-password-token";
        testToken.setTokenType(TokenType.PASSWORD_RESET);
        testToken.setExpiresAt(LocalDateTime.now().minusMinutes(1));

        when(tokenRepository.findAllByTokenTypeAndUsedAtIsNull(TokenType.PASSWORD_RESET))
                .thenReturn(Arrays.asList(testToken));
        when(passwordEncoder.matches(rawToken, testToken.getTokenHash())).thenReturn(true);

        // Act & Assert
        assertThrows(TokenAlreadyExpiredException.class,
                () -> verificationTokenService.verifyPasswordResetToken(rawToken));
    }

    // =================== Utility Tests ===================

    @Test
    void shouldInvalidateExistingUserTokens() {
        // Arrange
        when(tokenRepository.invalidateAllUserTokens(anyLong(), any(), any())).thenReturn(1);

        // Act
        verificationTokenService.invalidateUserToken(1L, TokenType.EMAIL_VERIFICATION);

        // Assert
        verify(tokenRepository).invalidateAllUserTokens(eq(1L), eq(TokenType.EMAIL_VERIFICATION), any());
    }

    @Test
    void shouldDeleteExpiredTokens() {
        // Arrange
        when(tokenRepository.deleteAllExpiredTokens(any(LocalDateTime.class))).thenReturn(5);

        // Act
        verificationTokenService.deleteExpiredTokens();

        // Assert
        verify(tokenRepository).deleteAllExpiredTokens(any(LocalDateTime.class));
    }
}
