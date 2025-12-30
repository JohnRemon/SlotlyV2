package com.example.SlotlyV2.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.SlotlyV2.exception.AccountNotVerifiedException;
import com.example.SlotlyV2.model.User;
import com.example.SlotlyV2.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void shouldNotLoadUsernameIfNotVerified() {
        // Arrange
        User testUser = createTestUser();
        testUser.setIsVerified(false);
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(AccountNotVerifiedException.class,
                () -> customUserDetailsService.loadUserByUsername(testUser.getEmail()));
    }

    @Test
    void shouldLoadUsernameSuccessfully() {
        // Arrange
        User testUser = createTestUser();
        testUser.setIsVerified(true);
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

        // Act
        UserDetails user = customUserDetailsService.loadUserByUsername(testUser.getEmail());

        // Assert
        assertNotNull(user);
        assertEquals(testUser.getEmail(), user.getUsername());
    }

    @Test
    void shouldReturnUserNotFoundException() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername(anyString()));
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
