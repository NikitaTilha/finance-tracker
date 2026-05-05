package com.example.finance_tracker.service;

import com.example.finance_tracker.dto.auth.LoginRequest;
import com.example.finance_tracker.dto.auth.LoginResponse;
import com.example.finance_tracker.entity.User;
import com.example.finance_tracker.exception.InvalidCredentialsException;
import com.example.finance_tracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void login_shouldReturnResponse_whenCredentialsAreValid() {
        User user = new User();
        user.setId(1L);
        user.setUsername("nikita");
        user.setPasswordHash(passwordEncoder.encode("12345678"));
        user.setCreatedAt(OffsetDateTime.now());

        LoginRequest request = new LoginRequest();
        request.setUsername("nikita");
        request.setPassword("12345678");

        when(userRepository.findByUsernameIgnoreCase("nikita"))
                .thenReturn(Optional.of(user));

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("nikita", response.getUsername());
        assertTrue(response.isAuthenticated());
        assertNotNull(response.getCreatedAt());
    }

    @Test
    void login_shouldThrowException_whenPasswordIsWrong() {
        User user = new User();
        user.setId(1L);
        user.setUsername("nikita");
        user.setPasswordHash(passwordEncoder.encode("12345678"));
        user.setCreatedAt(OffsetDateTime.now());

        LoginRequest request = new LoginRequest();
        request.setUsername("nikita");
        request.setPassword("wrongpass");

        when(userRepository.findByUsernameIgnoreCase("nikita"))
                .thenReturn(Optional.of(user));

        InvalidCredentialsException ex = assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        assertEquals("Неверный username или пароль", ex.getMessage());
    }

    @Test
    void login_shouldThrowException_whenUserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setUsername("nikita");
        request.setPassword("12345678");

        when(userRepository.findByUsernameIgnoreCase("nikita"))
                .thenReturn(Optional.empty());

        InvalidCredentialsException ex = assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        assertEquals("Неверный username или пароль", ex.getMessage());
    }
}