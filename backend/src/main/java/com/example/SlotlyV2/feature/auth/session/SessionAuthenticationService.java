package com.example.SlotlyV2.feature.auth.session;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.SlotlyV2.common.exception.auth.AccountNotVerifiedException;
import com.example.SlotlyV2.common.exception.auth.InvalidCredentialsException;
import com.example.SlotlyV2.feature.auth.dto.SessionLoginRequest;
import com.example.SlotlyV2.feature.user.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionAuthenticationService {
    private final SecurityContextRepository securityContextRepository;
    private final AuthenticationManager authenticationManager;

    @Transactional(readOnly = true)
    public User login(SessionLoginRequest request, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        Authentication authentication = authenticate(request.getEmail(), request.getPassword());
        User user = getUserFromAuthentication(authentication);

        validateLocalUser(user);
        persistSession(authentication, httpServletRequest, httpServletResponse);

        return user;
    }

    private Authentication authenticate(String email, String password) {
        try {
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));
        } catch (DisabledException e) {
            throw new AccountNotVerifiedException("Please verify your account first");
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
    }

    private User getUserFromAuthentication(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User user)) {
            throw new InvalidCredentialsException("Invalid user credentials");
        }
        return user;
    }

    private void validateLocalUser(User user) {
        if (user.isOAuthUser()) {
            throw new InvalidCredentialsException(
                    "This account uses Google sign-in. Please log in with Google.");
        }
    }

    private void persistSession(Authentication authentication, HttpServletRequest request,
            HttpServletResponse response) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context.getAuthentication() != null && context.getAuthentication().isAuthenticated()) {
            SecurityContextHolder.clearContext();
            securityContextRepository.saveContext(context, request, response);
        }
    }
}
