package com.example.SlotlyV2.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.SlotlyV2.exception.AccountNotVerifiedException;
import com.example.SlotlyV2.model.User;
import com.example.SlotlyV2.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException, AccountNotVerifiedException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

        if (!user.getIsVerified()) {
            throw new AccountNotVerifiedException("Please verify your account first");
        }

        return user;
    }

}
