package com.darksoul.concertsbooking.service;

import com.darksoul.concertsbooking.domain.UserAccount;
import com.darksoul.concertsbooking.repository.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserAccountRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    @Test
    void register_encodesPasswordAndSaves() {
        when(userRepository.existsByUsernameIgnoreCase("alice")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("hashed");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserAccount result = userService.register("alice", "secret");

        assertThat(result.getUsername()).isEqualTo("alice");
        assertThat(result.getPasswordHash()).isEqualTo("hashed");
        verify(userRepository).save(any(UserAccount.class));
    }

    @Test
    void register_trimsUsername() {
        when(userRepository.existsByUsernameIgnoreCase("alice")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserAccount result = userService.register("  alice  ", "secret");

        assertThat(result.getUsername()).isEqualTo("alice");
    }

    @Test
    void register_duplicateUsername_throws409() {
        when(userRepository.existsByUsernameIgnoreCase("alice")).thenReturn(true);

        assertThatThrownBy(() -> userService.register("alice", "secret"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Username is already taken");
    }

    @Test
    void getByUsername_found_returnsUser() {
        UserAccount user = UserAccount.builder().username("alice").passwordHash("hash").build();
        when(userRepository.findByUsernameIgnoreCase("alice")).thenReturn(Optional.of(user));

        UserAccount result = userService.getByUsername("alice");

        assertThat(result.getUsername()).isEqualTo("alice");
    }

    @Test
    void getByUsername_notFound_throws401() {
        when(userRepository.findByUsernameIgnoreCase("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getByUsername("ghost"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("User is not authenticated");
    }
}
