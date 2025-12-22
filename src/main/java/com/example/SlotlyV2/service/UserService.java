package com.example.SlotlyV2.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
}
