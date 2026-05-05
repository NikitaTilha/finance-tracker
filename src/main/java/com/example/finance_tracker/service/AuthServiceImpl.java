package com.example.finance_tracker.service;

import com.example.finance_tracker.dto.auth.LoginRequest;
import com.example.finance_tracker.dto.auth.LoginResponse;
import com.example.finance_tracker.entity.User;
import com.example.finance_tracker.exception.InvalidCredentialsException;
import com.example.finance_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public LoginResponse login(LoginRequest request) {
        String normalizedUsername = normalizeUsername(request.getUsername());

        User user = userRepository.findByUsernameIgnoreCase(normalizedUsername)
                .orElseThrow(() -> new InvalidCredentialsException("Неверный username или пароль"));

        boolean matches = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());

        if (!matches) {
            throw new InvalidCredentialsException("Неверный username или пароль");
        }

        return new LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getCreatedAt(),
                true
        );
    }

    private String normalizeUsername(String username) {
        return username.trim().toLowerCase(Locale.ROOT);
    }
}