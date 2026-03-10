// package com.example.SlotlyV2.service;
//
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyString;
// import static org.mockito.Mockito.never;
// import static org.mockito.Mockito.reset;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;
//
// import java.util.Optional;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.context.ApplicationEventPublisher;
// import org.springframework.security.crypto.password.PasswordEncoder;
//
// import com.example.SlotlyV2.common.exception.auth.InvalidTokenException;
// import
// com.example.SlotlyV2.common.exception.auth.TokenAlreadyExpiredException;
// import com.example.SlotlyV2.feature.auth.VerificationTokenService;
// import com.example.SlotlyV2.feature.email.event.PasswordResetEvent;
// import com.example.SlotlyV2.feature.password_reset.PasswordResetService;
// import com.example.SlotlyV2.feature.user.User;
// import com.example.SlotlyV2.feature.user.UserRepository;
// import com.example.SlotlyV2.feature.user.dto.PasswordResetConfirmRequest;
// import com.example.SlotlyV2.feature.user.dto.PasswordResetRequest;
//
// @ExtendWith(MockitoExtension.class)
// public class PasswordResetServiceTest {
// @Mock
// private UserRepository userRepository;
//
// @Mock
// private PasswordEncoder passwordEncoder;
//
// @Mock
// private VerificationTokenService verificationTokenService;
//
// @Mock
// private ApplicationEventPublisher applicationEventPublisher;
//
// @InjectMocks
// private PasswordResetService passwordResetService;
//
// @BeforeEach
// void setUp() {
// reset(userRepository, passwordEncoder, verificationTokenService,
// applicationEventPublisher);
// }
//
// @Test
// void shouldResetPasswordRequestSuccessfully() {
// // Arrange
// User testUser = createTestUser();
// when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
// when(verificationTokenService.generatePasswordResetToken(testUser))
// .thenReturn("raw-password-reset-token");
//
// PasswordResetRequest request = PasswordResetRequest.builder()
// .email("test@example.com")
// .build();
//
// // Act
// passwordResetService.resetPasswordRequest(request);
//
// // Assert
// verify(userRepository).findByEmail("test@example.com");
// verify(verificationTokenService).generatePasswordResetToken(testUser);
// verify(applicationEventPublisher).publishEvent(any(PasswordResetEvent.class));
// }
//
// @Test
// void shouldResetPasswordSuccessfully() {
// // Arrange
// User testUser = createTestUser();
// when(verificationTokenService.verifyPasswordResetToken("valid-token")).thenReturn(testUser);
// when(passwordEncoder.encode("newPassword123")).thenReturn("encoded-new-password");
// when(userRepository.save(any(User.class))).thenReturn(testUser);
// PasswordResetConfirmRequest request = new
// PasswordResetConfirmRequest("newPassword123", "newPassword123");
//
// // Act
// passwordResetService.resetPassword("valid-token", request);
//
// // Assert
// verify(verificationTokenService).verifyPasswordResetToken("valid-token");
// verify(passwordEncoder).encode("newPassword123");
// verify(userRepository).save(testUser);
// }
//
// @Test
// void shouldThrowInvalidTokenExceptionForInvalidPasswordResetToken() {
// // Arrange
// when(verificationTokenService.verifyPasswordResetToken(anyString()))
// .thenThrow(new InvalidTokenException("Invalid token"));
//
// PasswordResetConfirmRequest request = new
// PasswordResetConfirmRequest("newPassword123", "newPassword123");
//
// // Act & Assert
// assertThrows(InvalidTokenException.class, () ->
// passwordResetService.resetPassword(anyString(), request));
// }
//
// @Test
// void shouldThrowExpiredTokenExceptionForExpiredPasswordResetToken() {
// // Arrange
// when(verificationTokenService.verifyPasswordResetToken(anyString()))
// .thenThrow(new TokenAlreadyExpiredException("Token has expired"));
//
// PasswordResetConfirmRequest request = new
// PasswordResetConfirmRequest("newPassword123", "newPassword123");
//
// // Act & Assert
// assertThrows(TokenAlreadyExpiredException.class,
// () -> passwordResetService.resetPassword(anyString(), request));
// }
//
// @Test
// void shouldHandleResetPasswordRequestForNonExistentEmail() {
// // Arrange
// when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
// PasswordResetRequest request = PasswordResetRequest.builder()
// .email("nonexistent@example.com")
// .build();
//
// // Act
// passwordResetService.resetPasswordRequest(request);
//
// // Assert - Should not throw exception and should not publish event
// verify(userRepository).findByEmail("nonexistent@example.com");
// verify(verificationTokenService,
// never()).generatePasswordResetToken(any(User.class));
// verify(applicationEventPublisher, never()).publishEvent(any());
// }
//
// @Test
// void shouldThrowPasswordMismatchExceptionWhenPasswordsDoNotMatch() {
// // Arrange
// User testUser = createTestUser();
// when(verificationTokenService.verifyPasswordResetToken("valid-token")).thenReturn(testUser);
// PasswordResetConfirmRequest request = new
// PasswordResetConfirmRequest("newPassword123", "differentPassword");
//
// // Act and Assert
// assertThrows(PasswordMismatchException.class,
// () -> passwordResetService.resetPassword("valid-token", request));
//
// verify(verificationTokenService).verifyPasswordResetToken("valid-token");
// verify(passwordEncoder, never()).encode(anyString());
// verify(userRepository, never()).save(any(User.class));
// }
//
// User createTestUser() {
// return User.builder()
// .id(1L)
// .email("test@example.com")
// .password("encodedPassword")
// .firstName("John")
// .lastName("Doe")
// .build();
// }
// }
