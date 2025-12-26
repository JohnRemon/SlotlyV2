package com.example.SlotlyV2.service;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        reset(userRepository, passwordEncoder, authenticationManager);
    }

    // ============================= Register Tests =============================

    @Test
    void shouldRegisterUserSuccessfully() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByDisplayName(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User user = userService.registerUser(
                "test@example.com",
                "testUser",
                "password123",
                "John",
                "Doe",
                "UTC");

        // Assert
        assertNotNull(user);
        assertEquals("test@example.com", user.getEmail());
        assertEquals("testUser", user.getDisplayName());
        assertEquals("encodedPassword", user.getPassword(), "Password should be encoded");
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder, times(1)).encode("password123");
    }

    @Test
    void shouldThrowEmailAlreadyExistsException() {
        // Arrange
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Act and Assert
        assertThrows(UserAlreadyExistsException.class,
                () -> userService.registerUser(
                        "test@example.com",
                        "testUser",
                        "password123",
                        "John",
                        "Doe",
                        "UTC"));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldThrowUsernameAlreadyExistsException() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByDisplayName("testUser")).thenReturn(true);

        // Act and Assert
        assertThrows(UsernameAlreadyExistsException.class,
                () -> userService.registerUser(
                        "test@example.com",
                        "testUser",
                        "password123",
                        "John",
                        "Doe",
                        "UTC"));

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

        // Act
        User loggedInUser = userService.loginUser(
                "test@example.com", "password123");

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

        // Act and Assert
        assertThrows(InvalidCredentialsException.class,
                () -> userService.loginUser("test@example.com", "wrongPassword"));

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

        // Act and Assert
        assertThrows(InvalidCredentialsException.class,
                () -> userService.loginUser("wrongEmail@example.com", "password123"));

        // Verify
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("wrongEmail@example.com", "password123"));
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
}
