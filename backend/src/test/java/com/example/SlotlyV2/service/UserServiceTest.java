// package com.example.SlotlyV2.service;
//
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertFalse;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertNull;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyString;
// import static org.mockito.Mockito.mock;
// import static org.mockito.Mockito.never;
// import static org.mockito.Mockito.reset;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.ArgumentCaptor;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.context.ApplicationEventPublisher;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.crypto.password.PasswordEncoder;
//
// import
// com.example.SlotlyV2.common.exception.auth.UnauthorizedAccessException;
// import com.example.SlotlyV2.common.exception.user.UserAlreadyExistsException;
// import
// com.example.SlotlyV2.common.exception.user.UsernameAlreadyExistsException;
// import com.example.SlotlyV2.common.util.TimeZoneConverter;
// import com.example.SlotlyV2.feature.auth.VerificationTokenService;
// import com.example.SlotlyV2.feature.email.event.EmailVerificationEvent;
// import com.example.SlotlyV2.feature.schedule.ScheduleService;
// import com.example.SlotlyV2.feature.user.User;
// import com.example.SlotlyV2.feature.user.UserRepository;
// import com.example.SlotlyV2.feature.user.UserService;
// import com.example.SlotlyV2.feature.user.dto.RegisterRequest;
//
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpSession;
//
// @ExtendWith(MockitoExtension.class)
// public class UserServiceTest {
// @Mock
// private UserRepository userRepository;
//
// @Mock
// private PasswordEncoder passwordEncoder;
//
// @Mock
// private AuthenticationManager authenticationManager;
//
// @Mock
// private VerificationTokenService verificationTokenService;
//
// @Mock
// private ApplicationEventPublisher applicationEventPublisher;
//
// @Mock
// private ScheduleService scheduleService;
//
// @Mock
// private TimeZoneConverter timeZoneConverter;
//
// @InjectMocks
// private UserService userService;
//
// @BeforeEach
// void setUp() {
// reset(userRepository, passwordEncoder, authenticationManager,
// verificationTokenService,
// applicationEventPublisher, scheduleService, timeZoneConverter);
// }
//
// // ============================= Register Tests =============================
// @Test
// void shouldRegisterUserSuccessfully() {
// // Arrange
// when(userRepository.existsByEmail(anyString())).thenReturn(false);
// when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
// when(userRepository.save(any(User.class))).thenAnswer(i -> {
// User user = i.getArgument(0);
// user.setId(1L);
// return user;
// });
// when(verificationTokenService.generateEmailVerificationToken(any(User.class)))
// .thenReturn("raw-verification-token");
//
// RegisterRequest request = new RegisterRequest(
// "test@example.com",
// "password123",
// "John",
// "Doe",
// "UTC");
//
// // Act
// User user = userService.registerUser(request);
//
// // Assert - Basic Fields
// assertNotNull(user);
// assertEquals("test@example.com", user.getEmail());
// assertEquals("encodedPassword", user.getPassword());
// assertEquals("John", user.getFirstName());
// assertEquals("Doe", user.getLastName());
//
// // Assert - Verification
// assertFalse(user.isVerified(), "Verification should be set to false");
//
// // Assert - Event Publish
// ArgumentCaptor<EmailVerificationEvent> eventCaptor =
// ArgumentCaptor.forClass(EmailVerificationEvent.class);
// verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
//
// EmailVerificationEvent event = eventCaptor.getValue();
// assertEquals(user.getEmail(),
// event.getUserEmailVerificationDTO().getEmail());
// assertEquals("raw-verification-token",
// event.getUserEmailVerificationDTO().getToken());
//
// // Verify Repository Interactions
// verify(userRepository).existsByEmail(request.getEmail());
// verify(passwordEncoder).encode(request.getPassword());
// verify(userRepository).save(any(User.class));
// verify(verificationTokenService).generateEmailVerificationToken(any(User.class));
// }
//
// @Test
// void shouldThrowEmailAlreadyExistsException() {
// // Arrange
// when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
// RegisterRequest request = new RegisterRequest(
// "test@example.com",
// "password123",
// "John",
// "Doe",
// "UTC");
//
// // Act and Assert
// assertThrows(UserAlreadyExistsException.class,
// () -> userService.registerUser(request));
//
// verify(userRepository, never()).save(any(User.class));
// verify(verificationTokenService,
// never()).generateEmailVerificationToken(any(User.class));
// }
//
// @Test
// void shouldThrowUsernameAlreadyExistsException() {
// // Arrange
// when(userRepository.existsByEmail(anyString())).thenReturn(false);
// RegisterRequest request = new RegisterRequest(
// "test@example.com",
// "password123",
// "John",
// "Doe",
// "UTC");
//
// // Act and Assert
// assertThrows(UsernameAlreadyExistsException.class,
// () -> userService.registerUser(request));
//
// verify(userRepository, never()).save(any(User.class));
// verify(verificationTokenService,
// never()).generateEmailVerificationToken(any(User.class));
// }
//
// // ============================= Login Tests =============================
// // @Test
// // void shouldLoginUserSuccessfully() {
// // // Arrange
// // User testUser = User.builder()
// // .id(1L)
// // .email("test@example.com")
// // .password("encodedPassword")
// // .firstName("John")
// // .lastName("Doe")
// // .isVerified(true)
// // .build();
// //
// // Authentication authentication = mock(Authentication.class);
// // when(authentication.getPrincipal()).thenReturn(testUser);
// //
// when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
// // .thenReturn(authentication);
// //
// // LoginRequest request = new LoginRequest(
// // "test@example.com",
// // "password123");
// //
// // // Act
// // User loggedInUser = userService.loginUser(request);
// //
// // // Assert
// // assertNotNull(loggedInUser);
// // assertEquals("test@example.com", loggedInUser.getEmail());
// // assertEquals("testUser", loggedInUser.getDisplayName());
// //
// // // Verify
// //
// verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
// // }
// //
// // @Test
// // void shouldNotLoginWithWrongPassword() {
// // // Arrange
// //
// when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
// // .thenThrow(new BadCredentialsException("Bad credentials"));
// //
// // LoginRequest request = new LoginRequest(
// // "test@example.com",
// // "wrongPassword");
// //
// // // Act and Assert
// // assertThrows(InvalidCredentialsException.class,
// // () -> userService.loginUser(request));
// //
// //
// verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
// // }
// //
// // @Test
// // void shouldNotLoginWithWrongEmail() {
// // // Arrange
// // when(authenticationManager.authenticate(
// // new UsernamePasswordAuthenticationToken("wrongEmail@example.com",
// // "password123")))
// // .thenThrow(new BadCredentialsException("Invalid credentials"));
// //
// // LoginRequest request = new LoginRequest(
// // "wrongEmail@example.com",
// // "password123");
// //
// // // Act and Assert
// // assertThrows(InvalidCredentialsException.class,
// // () -> userService.loginUser(request));
// //
// // // Verify
// // verify(authenticationManager).authenticate(
// // new UsernamePasswordAuthenticationToken("wrongEmail@example.com",
// // "password123"));
// // }
// //
// // @Test
// // void shouldNotLoginWhenNotVerified() {
// // // Arrange
// // User testUser = User.builder()
// // .id(1L)
// // .email("test@example.com")
// // .isVerified(false)
// // .build();
// //
// // LoginRequest request = new LoginRequest(
// // "test@example.com",
// // "password123");
// //
// // Authentication authentication = mock(Authentication.class);
// // when(authentication.getPrincipal()).thenReturn(testUser);
// // when(authenticationManager.authenticate(
// // new UsernamePasswordAuthenticationToken("test@example.com",
// // "password123"))).thenReturn(authentication);
// //
// // // Act & Assert
// // assertThrows(AccountNotVerifiedException.class,
// // () -> userService.loginUser(request));
// //
// // assertNull(SecurityContextHolder.getContext().getAuthentication());
// // }
//
// // ========================= Current User Tests =========================
// @Test
// void shouldGetCurrentUserSuccessfully() {
// // Arrange
// User testUser = User.builder()
// .id(1L)
// .email("test@example.com")
// .build();
//
// Authentication authentication = mock(Authentication.class);
// when(authentication.getPrincipal()).thenReturn(testUser);
// when(authentication.isAuthenticated()).thenReturn(true);
//
// SecurityContextHolder.getContext().setAuthentication(authentication);
//
// // Act
// User currentUser = userService.getCurrentUser();
//
// // Assert
// assertNotNull(currentUser);
// assertEquals("test@example.com", currentUser.getEmail());
//
// // Clean
// SecurityContextHolder.clearContext();
// }
//
// @Test
// void
// shouldThrowUnauthorizedAccessExceptionWhenUnauthenticatedForGetCurrentUser()
// {
// // Arrange
// SecurityContextHolder.getContext().setAuthentication(null);
//
// // Act & Assert
// assertThrows(UnauthorizedAccessException.class,
// () -> userService.getCurrentUser());
//
// // Clean
// SecurityContextHolder.clearContext();
// }
//
// // ============================= Logout Tests =============================
// @Test
// void shouldLogoutUserWithSession() {
// // Arrange
// Authentication authentication = mock(Authentication.class);
// when(authentication.getPrincipal()).thenReturn(User.builder().build());
// when(authentication.isAuthenticated()).thenReturn(true);
//
// SecurityContextHolder.getContext().setAuthentication(authentication);
//
// HttpSession session = mock(HttpSession.class);
// HttpServletRequest request = mock(HttpServletRequest.class);
//
// when(request.getSession(false)).thenReturn(session);
//
// // Act
// userService.logout(request);
//
// // Assert
// verify(session).invalidate();
// assertNull(SecurityContextHolder.getContext().getAuthentication());
// SecurityContextHolder.clearContext();
// }
//
// @Test
// void shouldLogoutUserWithoutSession() {
// // Arrange
// Authentication authentication = mock(Authentication.class);
// when(authentication.getPrincipal()).thenReturn(User.builder().build());
// when(authentication.isAuthenticated()).thenReturn(true);
//
// SecurityContextHolder.getContext().setAuthentication(authentication);
//
// HttpServletRequest request = mock(HttpServletRequest.class);
// when(request.getSession(false)).thenReturn(null);
//
// // Act
// userService.logout(request);
//
// // Assert
// assertNull(SecurityContextHolder.getContext().getAuthentication());
// SecurityContextHolder.clearContext();
// }
//
// @Test
// void
// shouldThrowUnauthorizedAccessExceptionWhenUnauthenticatedForLogoutWithSession()
// {
// // Assert
// SecurityContextHolder.getContext().setAuthentication(null);
// HttpServletRequest request = mock(HttpServletRequest.class);
//
// // Act & Assert
// assertThrows(UnauthorizedAccessException.class,
// () -> userService.logout(request));
//
// // Clean
// SecurityContextHolder.clearContext();
// }
//
// @Test
// void
// shouldThrowUnauthorizedAccessExceptionWhenUnauthenticatedForLogoutWithoutSession()
// {
// // Assert
// SecurityContextHolder.getContext().setAuthentication(null);
// HttpServletRequest request = mock(HttpServletRequest.class);
//
// // Act & Assert
// assertThrows(UnauthorizedAccessException.class,
// () -> userService.logout(request));
//
// // Clean
// SecurityContextHolder.clearContext();
// }
//
// // Helper
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
