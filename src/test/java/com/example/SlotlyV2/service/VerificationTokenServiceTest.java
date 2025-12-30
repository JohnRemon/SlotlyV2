package com.example.SlotlyV2.service;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.SlotlyV2.exception.AccountAlreadyVerifiedException;
import com.example.SlotlyV2.exception.InvalidTokenException;
import com.example.SlotlyV2.exception.TokenAlreadyExpiredException;
import com.example.SlotlyV2.model.User;
import com.example.SlotlyV2.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class VerificationTokenServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private VerificationTokenService verificationTokenService;

    @BeforeEach
    void setUp() {
        reset(userRepository);
    }

    // ============== generateEmailVerificationToken ==========

    @Test
    void shouldGenerateEmailVerificationTokenSuccessfully() {
        // Arrange
        User testUser = createTestUser();
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = verificationTokenService.generateEmailVerificationToken(testUser);

        // Assert
        assertNotNull(result.getEmailVerificationToken());
        assertNotNull(result.getEmailVerificationTokenExpiresAt());
        verify(userRepository).save(testUser);
    }

    @Test
    void shouldSetTokenExpirationTo24Hours() {
        // Arrange
        User testUser = createTestUser();
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = verificationTokenService.generateEmailVerificationToken(testUser);

        // Assert
        assertTrue(result.getEmailVerificationTokenExpiresAt().isAfter(LocalDateTime.now()));
        assertTrue(result.getEmailVerificationTokenExpiresAt().isBefore(LocalDateTime.now().plusHours(25)));
    }

    @Test
    void shouldOverwriteExistingTokenWhenGeneratingNew() {
        // Arrange
        User testUser = createTestUser();
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result1 = verificationTokenService.generateEmailVerificationToken(testUser);
        String tokenBefore = result1.getEmailVerificationToken();

        User result2 = verificationTokenService.generateEmailVerificationToken(testUser);
        String tokenAfter = result2.getEmailVerificationToken();

        // Assert
        assertNotEquals(tokenBefore, tokenAfter); // Should generate different tokens
        assertNotNull(tokenAfter);
    }

    @Test
    void shouldGenerateUniqueUUIDToken() {
        // Arrange
        User user1 = createTestUser();
        User user2 = createTestUser();
        user2.setEmail("different@example.com");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result1 = verificationTokenService.generateEmailVerificationToken(user1);
        User result2 = verificationTokenService.generateEmailVerificationToken(user2);

        // Assert
        assertNotEquals(result1.getEmailVerificationToken(), result2.getEmailVerificationToken());
    }

    // ================== verifyVerificationToken ===================

    @Test
    void shouldVerifyEmailWithValidToken() {
        // Arrange
        User testUser = createTestUser();
        testUser.setEmailVerificationToken("valid-token-123");
        testUser.setEmailVerificationTokenExpiresAt(LocalDateTime.now().plusHours(24));
        testUser.setIsVerified(false);

        when(userRepository.findByEmailVerificationToken("valid-token-123"))
                .thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        Boolean result = verificationTokenService.verifyVerificationToken("valid-token-123");

        // Assert
        assertTrue(result);
        assertTrue(testUser.getIsVerified());
        assertNull(testUser.getEmailVerificationToken());
        assertNull(testUser.getEmailVerificationTokenExpiresAt());
        verify(userRepository).save(testUser);
    }

    @Test
    void shouldThrowInvalidTokenExceptionWhenTokenNotFound() {
        // Arrange
        when(userRepository.findByEmailVerificationToken("invalid-token"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidTokenException.class,
                () -> verificationTokenService.verifyVerificationToken("invalid-token"));
    }

    @Test
    void shouldThrowAccountAlreadyVerifiedExceptionWhenUserAlreadyVerified() {
        // Arrange
        User testUser = createTestUser();
        testUser.setEmailVerificationToken("valid-token-123");
        testUser.setEmailVerificationTokenExpiresAt(LocalDateTime.now().plusHours(24));
        testUser.setIsVerified(true);

        when(userRepository.findByEmailVerificationToken("valid-token-123"))
                .thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(AccountAlreadyVerifiedException.class,
                () -> verificationTokenService.verifyVerificationToken("valid-token-123"));
    }

    @Test
    void shouldThrowTokenAlreadyExpiredExceptionWhenTokenExpired() {
        // Arrange
        User testUser = createTestUser();
        testUser.setEmailVerificationToken("expired-token");
        testUser.setEmailVerificationTokenExpiresAt(LocalDateTime.now().minusHours(1));
        testUser.setIsVerified(false);

        when(userRepository.findByEmailVerificationToken("expired-token"))
                .thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(TokenAlreadyExpiredException.class,
                () -> verificationTokenService.verifyVerificationToken("expired-token"));
    }

    // ================== generatePasswordVerificationToken ===================

    @Test
    void shouldGeneratePasswordVerificationTokenSuccessfully() {
        // Arrange
        User testUser = createTestUser();
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = verificationTokenService.generatePasswordVerificationToken(testUser);

        // Assert
        assertNotNull(result.getPasswordVerificationToken());
        assertNotNull(result.getPasswordVerificationTokenExpiresAt());
        verify(userRepository).save(testUser);
    }

    @Test
    void shouldSetPasswordTokenExpirationTo30Minutes() {
        // Arrange
        User testUser = createTestUser();
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = verificationTokenService.generatePasswordVerificationToken(testUser);

        // Assert
        assertTrue(result.getPasswordVerificationTokenExpiresAt().isAfter(LocalDateTime.now()));
        assertTrue(result.getPasswordVerificationTokenExpiresAt().isBefore(LocalDateTime.now().plusMinutes(31)));
    }

    @Test
    void shouldOverwriteExistingPasswordTokenWhenGeneratingNew() {
        // Arrange
        User testUser = createTestUser();
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result1 = verificationTokenService.generatePasswordVerificationToken(testUser);
        String tokenBefore = result1.getPasswordVerificationToken();

        User result2 = verificationTokenService.generatePasswordVerificationToken(testUser);
        String tokenAfter = result2.getPasswordVerificationToken();

        // Assert
        assertNotEquals(tokenBefore, tokenAfter);
        assertNotNull(tokenAfter);
    }

    @Test
    void shouldThrowInvalidTokenExceptionForInvalidPasswordToken() {
        // Arrange
        when(userRepository.findByPasswordVerificationToken("invalid-password-token"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidTokenException.class,
                () -> verificationTokenService.verifyPasswordVerificationToken("invalid-password-token"));
    }

    @Test
    void shouldThrowTokenAlreadyExpiredExceptionForExpiredPasswordToken() {
        // Arrange
        User testUser = createTestUser();
        testUser.setPasswordVerificationToken("expired-password-token");
        testUser.setPasswordVerificationTokenExpiresAt(LocalDateTime.now().minusMinutes(1));

        when(userRepository.findByPasswordVerificationToken("expired-password-token"))
                .thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(TokenAlreadyExpiredException.class,
                () -> verificationTokenService.verifyPasswordVerificationToken("expired-password-token"));
    }

    @Test
    void shouldVerifyPasswordVerificationTokenSuccessfully() {
        // Arrange
        User testUser = createTestUser();
        testUser.setPasswordVerificationToken("valid-password-token");
        testUser.setPasswordVerificationTokenExpiresAt(LocalDateTime.now().plusMinutes(30));

        when(userRepository.findByPasswordVerificationToken("valid-password-token"))
                .thenReturn(Optional.of(testUser));

        // Act
        User result = verificationTokenService.verifyPasswordVerificationToken("valid-password-token");

        // Assert
        assertEquals(testUser, result);
    }

    User createTestUser() {
        User testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setDisplayName("testUser");
        testUser.setPassword("encodedPassword");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        return testUser;
    }
}
