package com.example.SlotlyV2.service;

import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.SlotlyV2.dto.LoginRequest;
import com.example.SlotlyV2.dto.PasswordResetConfirmRequest;
import com.example.SlotlyV2.dto.PasswordResetRequest;
import com.example.SlotlyV2.dto.RegisterRequest;
import com.example.SlotlyV2.event.EmailVerificationEvent;
import com.example.SlotlyV2.event.PasswordResetEvent;
import com.example.SlotlyV2.exception.InvalidCredentialsException;
import com.example.SlotlyV2.exception.UnauthorizedAccessException;
import com.example.SlotlyV2.exception.UserAlreadyExistsException;
import com.example.SlotlyV2.exception.UsernameAlreadyExistsException;
import com.example.SlotlyV2.model.User;
import com.example.SlotlyV2.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private VerificationTokenService verificationTokenService;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        reset(userRepository, passwordEncoder, authenticationManager, verificationTokenService,
                applicationEventPublisher);
    }

    // ============================= Register Tests =============================

    @Test
    void shouldRegisterUserSuccessfully() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByDisplayName(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(verificationTokenService.generateEmailVerificationToken(any(User.class)))
                .thenAnswer(invocation -> {
                    User testUser = invocation.getArgument(0);
                    testUser.setId(1L);
                    testUser.setEmailVerificationToken("mock-token");
                    testUser.setEmailVerificationTokenExpiresAt(LocalDateTime.now().plusHours(24));
                    return testUser;
                });

        RegisterRequest request = new RegisterRequest(
                "test@example.com",
                "testUser",
                "password123",
                "John",
                "Doe",
                "UTC");

        // Act
        User user = userService.registerUser(request);

        // Assert - Basic Fields
        assertNotNull(user);
        assertEquals("test@example.com", user.getEmail());
        assertEquals("testUser", user.getDisplayName());
        assertEquals("encodedPassword", user.getPassword());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());

        // Assert - Verification
        assertFalse(user.getIsVerified(), "Verification should be set to false");
        assertNotNull(user.getEmailVerificationToken(), "Verification token should not be null");
        assertNotNull(user.getEmailVerificationTokenExpiresAt(), "Verification Token Expiry should not be null");
        assertTrue(user.getEmailVerificationTokenExpiresAt().isAfter(LocalDateTime.now()),
                "Verification Token Expiry should be in the future");

        // Assert - Event Publish
        ArgumentCaptor<EmailVerificationEvent> eventCaptor = ArgumentCaptor.forClass(EmailVerificationEvent.class);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());

        EmailVerificationEvent event = eventCaptor.getValue();
        assertEquals(user.getDisplayName(), event.getUserRegistrationVerificationData().getDisplayName());
        assertEquals(user.getEmail(), event.getUserRegistrationVerificationData().getEmail());
        assertEquals(user.getEmailVerificationToken(), event.getUserRegistrationVerificationData().getToken());

        // Verify Repository Interactions
        verify(userRepository).existsByEmail(request.getEmail());
        verify(userRepository).existsByDisplayName(request.getDisplayName());
        verify(passwordEncoder).encode(request.getPassword());
        verify(verificationTokenService).generateEmailVerificationToken(any(User.class));
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
    }

    @Test
    void shouldThrowEmailAlreadyExistsException() {
        // Arrange
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        RegisterRequest request = new RegisterRequest(
                "test@example.com",
                "testUser",
                "password123",
                "John",
                "Doe",
                "UTC");

        // Act and Assert
        assertThrows(UserAlreadyExistsException.class,
                () -> userService.registerUser(request));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldThrowUsernameAlreadyExistsException() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByDisplayName("testUser")).thenReturn(true);

        RegisterRequest request = new RegisterRequest(
                "test@example.com",
                "testUser",
                "password123",
                "John",
                "Doe",
                "UTC");

        // Act and Assert
        assertThrows(UsernameAlreadyExistsException.class,
                () -> userService.registerUser(request));

        verify(userRepository, never()).save(any(User.class));
    }

    // ============================= Login Tests =============================

    @Test
    void shouldLoginUserSuccessfully() {
        // Arrange
        User testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setDisplayName("testUser");
        testUser.setPassword("encodedPassword");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");

        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn(testUser);
        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("test@example.com", "password123"))).thenReturn(authentication);

        LoginRequest request = new LoginRequest(
                "test@example.com",
                "password123");

        // Act
        User loggedInUser = userService.loginUser(request);

        // Assert
        assertNotNull(loggedInUser);
        assertEquals("test@example.com", loggedInUser.getEmail());
        assertEquals("testUser", loggedInUser.getDisplayName());

        // Verify
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("test@example.com", "password123"));
    }

    @Test
    void shouldNotLoginWithWrongPassword() {
        // Arrange
        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("test@example.com", "wrongPassword")))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        LoginRequest request = new LoginRequest(
                "test@example.com",
                "wrongPassword");

        // Act and Assert
        assertThrows(InvalidCredentialsException.class,
                () -> userService.loginUser(request));

        // Verify
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("test@example.com", "wrongPassword"));
    }

    @Test
    void shouldNotLoginWithWrongEmail() {
        // Arrange
        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("wrongEmail@example.com", "password123")))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        LoginRequest request = new LoginRequest(
                "wrongEmail@example.com",
                "password123");

        // Act and Assert
        assertThrows(InvalidCredentialsException.class,
                () -> userService.loginUser(request));

        // Verify
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("wrongEmail@example.com", "password123"));
    }
    // ========================= Password Reset Tests =======================

    @Test
    void shouldResetPasswordRequestSuccessfully() {
        // Arrange
        User testUser = createTestUser();
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(verificationTokenService.generatePasswordVerificationToken(testUser)).thenReturn(testUser);

        PasswordResetRequest request = new PasswordResetRequest();
        request.setEmail("test@example.com");

        // Act
        userService.resetPasswordRequest(request);

        // Assert
        verify(userRepository).findByEmail("test@example.com");
        verify(verificationTokenService).generatePasswordVerificationToken(testUser);
        verify(applicationEventPublisher).publishEvent(any(PasswordResetEvent.class));
    }

    @Test
    void shouldHandleResetPasswordRequestForNonExistentEmail() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        PasswordResetRequest request = new PasswordResetRequest();
        request.setEmail("nonexistent@example.com");

        // Act
        userService.resetPasswordRequest(request);

        // Assert - Should not throw exception and should not publish event
        verify(userRepository).findByEmail("nonexistent@example.com");
        verify(verificationTokenService, never()).generatePasswordVerificationToken(any(User.class));
        verify(applicationEventPublisher, never()).publishEvent(any());
    }

    @Test
    void shouldResetPasswordSuccessfully() {
        // Arrange
        User testUser = createTestUser();
        when(verificationTokenService.verifyPasswordVerificationToken("valid-token")).thenReturn(testUser);
        when(passwordEncoder.encode("newPassword123")).thenReturn("encoded-new-password");

        PasswordResetConfirmRequest request = new PasswordResetConfirmRequest("newPassword123", "newPassword123");

        // Act
        userService.resetPassword("valid-token", request);

        // Assert
        verify(verificationTokenService).verifyPasswordVerificationToken("valid-token");
        assertNull(testUser.getPasswordVerificationToken());
        assertNull(testUser.getPasswordVerificationTokenExpiresAt());
        verify(passwordEncoder).encode("newPassword123");
        verify(userRepository).save(testUser);
    }

    @Test
    void shouldThrowPasswordMismatchExceptionWhenPasswordsDoNotMatch() {
        // Arrange
        User testUser = createTestUser();
        when(verificationTokenService.verifyPasswordVerificationToken("valid-token")).thenReturn(testUser);

        PasswordResetConfirmRequest request = new PasswordResetConfirmRequest("newPassword123", "differentPassword");

        // Act and Assert
        assertThrows(com.example.SlotlyV2.exception.PasswordMismatchException.class,
                () -> userService.resetPassword("valid-token", request));

        verify(verificationTokenService).verifyPasswordVerificationToken("valid-token");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    // ========================= Current User Tests =========================

    @Test
    void shouldGetCurrentUserSuccessfully() {
        // Arrange
        User testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setDisplayName("testUser");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(testUser);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        User currentUser = userService.getCurrentUser();

        // Assert
        assertNotNull(currentUser);
        assertEquals("test@example.com", currentUser.getEmail());
        assertEquals("testUser", currentUser.getDisplayName());

        // Clean
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldThrowUnauthorizedAccessExceptionWhenUnauthenticatedForGetCurrentUser() {
        // Arrange
        SecurityContextHolder.getContext().setAuthentication(null);

        // Act and Assert
        assertThrows(UnauthorizedAccessException.class,
                () -> userService.getCurrentUser());

        // Clean
        SecurityContextHolder.clearContext();
    }

    // ============================= Logout Tests =============================

    @Test
    void shouldLogoutUserWithSession() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(new User());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        HttpSession session = mock(HttpSession.class);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getSession(false)).thenReturn(session);

        // Act
        userService.logout(request);

        // Assert
        verify(session).invalidate();
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldLogoutUserWithoutSession() {

        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(new User());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getSession(false)).thenReturn(null);

        // Act
        userService.logout(request);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldThrowUnauthorizedAccessExceptionWhenUnauthenticatedForLogoutWithSession() {
        // Assert
        SecurityContextHolder.getContext().setAuthentication(null);

        HttpServletRequest request = mock(HttpServletRequest.class);

        // Act and Assert
        assertThrows(UnauthorizedAccessException.class,
                () -> userService.logout(request));

        // Clean
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldThrowUnauthorizedAccessExceptionWhenUnauthenticatedForLogoutWithoutSession() {
        // Assert
        SecurityContextHolder.getContext().setAuthentication(null);
        HttpServletRequest request = mock(HttpServletRequest.class);

        // Act and Assert
        assertThrows(UnauthorizedAccessException.class,
                () -> userService.logout(request));

        // Clean
        SecurityContextHolder.clearContext();
    }

    // Helper
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
