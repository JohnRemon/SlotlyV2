package com.example.SlotlyV2.service;

import java.util.ArrayList;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.SlotlyV2.exception.InvalidCredentialsException;
import com.example.SlotlyV2.exception.UserAlreadyExistsException;
import com.example.SlotlyV2.exception.UsernameAlreadyExistsException;
import com.example.SlotlyV2.model.User;
import com.example.SlotlyV2.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String email, String username, String password, String firstName, String lastName,
            String timeZone) {
        // Check if email already exsists
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("User Already Exists. Please Login");
        }

        // Check if username already exists
        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException("Username Already Exists. Please Choose Another One");
        }

        // Create the user
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setTimeZone(timeZone);

        // Save and Return the user
        return userRepository.save(user);
    }

    public User loginUser(String email, String password) {
        // Find the user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid Email or Password"));

        // Verify the password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid Email or Password");
        }

        // Create authentication token and set it in security context
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null,
                new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        return user;
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }
}
