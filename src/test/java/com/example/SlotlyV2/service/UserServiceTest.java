package com.example.SlotlyV2.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.SlotlyV2.exception.UserAlreadyExistsException;
import com.example.SlotlyV2.exception.UsernameAlreadyExistsException;
import com.example.SlotlyV2.model.User;
import com.example.SlotlyV2.repository.UserRepository;

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
}
