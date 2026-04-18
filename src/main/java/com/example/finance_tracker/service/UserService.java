package com.example.finance_tracker.service;

import com.example.finance_tracker.dto.user.CreateUserRequest;
import com.example.finance_tracker.dto.user.UserResponse;
import com.example.finance_tracker.entity.User;
import com.example.finance_tracker.mapper.UserMapper;
import com.example.finance_tracker.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        String normalizedUsername = normalizeUsername(request.getUsername());
        String passwordHash = passwordEncoder.encode(request.getPassword());

        userRepository.findByUsernameIgnoreCase(normalizedUsername)
                .ifPresent(user -> {
                    throw new IllegalStateException("Пользователь с таким username уже существует");
                });

        User user = new User();
        user.setUsername(normalizedUsername);
        user.setPasswordHash(passwordHash);

        User savedUser = userRepository.save(user);
        return UserMapper.toResponse(savedUser);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAllByOrderByIdAsc()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));

        return UserMapper.toResponse(user);
    }

    private String normalizeUsername(String username) {
        return username.trim().toLowerCase(Locale.ROOT);
    }
}