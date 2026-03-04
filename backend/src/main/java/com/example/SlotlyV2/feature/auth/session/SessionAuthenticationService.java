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

    @Transactional
    public User login(SessionLoginRequest sessionLoginRequest, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        try {
            // Authenticate the user with email and password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            sessionLoginRequest.getEmail(), sessionLoginRequest.getPassword()));

            User user = (User) authentication.getPrincipal();

            // check if using oauth
            if (user.isOAuthUser()) {
                throw new InvalidCredentialsException(
                        "This account uses Google sign-in. Please log in with Google.");
            }

            // persist session
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            httpServletRequest.getSession(true);
            securityContextRepository.saveContext(context, httpServletRequest, httpServletResponse);

            return user;

        } catch (DisabledException e) {
            throw new AccountNotVerifiedException("please verify your account first");
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid Credentials");
        }
    }
}
