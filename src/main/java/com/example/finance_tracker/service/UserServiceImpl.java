package com.example.finance_tracker.service;

import com.example.finance_tracker.dto.user.CreateUserRequest;
import com.example.finance_tracker.dto.user.UserResponse;
import com.example.finance_tracker.entity.User;
import com.example.finance_tracker.exception.ApiException;
import com.example.finance_tracker.mapper.UserMapper;
import com.example.finance_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByUsernameIgnoreCase(request.getUsername())) {
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "Пользователь с таким username уже существует"
            );
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAllByOrderByIdAsc()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public void deleteCurrentUser(Long currentUserId, String rawPassword) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new ApiException(
                    HttpStatus.UNAUTHORIZED,
                    "Неверный пароль"
            );
        }

        userRepository.delete(user);
    }
}