package com.darksoul.concertsbooking.service;

import com.darksoul.concertsbooking.domain.UserAccount;
import com.darksoul.concertsbooking.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserAccountRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAccount register(String username, String password) {
        var normalizedUsername = username.trim();
        if (userRepository.existsByUsernameIgnoreCase(normalizedUsername)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already taken");
        }

        return userRepository.save(UserAccount.builder()
                .username(normalizedUsername)
                .passwordHash(passwordEncoder.encode(password))
                .build());
    }

    public UserAccount getByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated"));
    }
}
