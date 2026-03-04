package com.example.SlotlyV2.feature.auth.session;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

import com.example.SlotlyV2.common.exception.auth.AccountNotVerifiedException;
import com.example.SlotlyV2.common.exception.auth.InvalidCredentialsException;
import com.example.SlotlyV2.feature.auth.dto.SessionLoginRequest;
import com.example.SlotlyV2.feature.user.User;
import com.example.SlotlyV2.feature.user.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionAuthenticationService {
    private final UserRepository userRepository;
    private final SecurityContextRepository securityContextRepository;
    private final AuthenticationManager authenticationManager;

    public User login(SessionLoginRequest sessionLoginRequest, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {
        User user = userRepository.findByEmail(sessionLoginRequest.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid Credentials"));

        if (user.isOAuthUser()) {
            throw new InvalidCredentialsException(
                    "This account uses Google sign-in. Please log in with Google.");
        }

        if (!user.isVerified()) {
            throw new AccountNotVerifiedException("Please verify your account first");
        }

        try {
            // Authenticate the user with email and password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            sessionLoginRequest.getEmail(), sessionLoginRequest.getPassword()));

            // set the context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Ensure session is created so JSESSIONID is issued
            httpServletRequest.getSession(true);

            // Persist SecurityContext to the session
            SecurityContext context = SecurityContextHolder.getContext();
            securityContextRepository.saveContext(context, httpServletRequest, httpServletResponse);

            // return the user
            return (User) authentication.getPrincipal();

        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid Credentials");
        }
    }
}
