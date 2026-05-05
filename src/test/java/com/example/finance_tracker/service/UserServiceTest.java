package com.example.finance_tracker.service;

import com.example.finance_tracker.dto.user.CreateUserRequest;
import com.example.finance_tracker.dto.user.UserResponse;
import com.example.finance_tracker.entity.User;
import com.example.finance_tracker.exception.ConflictException;
import com.example.finance_tracker.exception.UnauthorizedException;
import com.example.finance_tracker.mapper.UserMapper;
import com.example.finance_tracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_shouldReturnResponse_whenDataIsValid() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("nikita");
        request.setPassword("12345678");

        OffsetDateTime createdAt = OffsetDateTime.now();

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("nikita");
        savedUser.setPasswordHash("encoded-password");
        savedUser.setCreatedAt(createdAt);

        UserResponse expectedResponse = new UserResponse(
                1L,
                "nikita",
                createdAt
        );

        when(userRepository.existsByUsernameIgnoreCase("nikita")).thenReturn(false);
        when(passwordEncoder.encode("12345678")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(expectedResponse);

        UserResponse response = userService.createUser(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("nikita", response.getUsername());
        assertEquals(createdAt, response.getCreatedAt());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_shouldThrowConflict_whenUsernameAlreadyExists() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("nikita");
        request.setPassword("12345678");

        when(userRepository.existsByUsernameIgnoreCase("nikita")).thenReturn(true);

        ConflictException ex = assertThrows(
                ConflictException.class,
                () -> userService.createUser(request)
        );

        assertEquals("Пользователь с таким username уже существует", ex.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_shouldReturnResponse_whenUserExists() {
        OffsetDateTime createdAt = OffsetDateTime.now();

        User user = new User();
        user.setId(1L);
        user.setUsername("nikita");
        user.setPasswordHash("hash");
        user.setCreatedAt(createdAt);

        UserResponse expectedResponse = new UserResponse(
                1L,
                "nikita",
                createdAt
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(expectedResponse);

        UserResponse response = userService.getUserById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("nikita", response.getUsername());
        assertEquals(createdAt, response.getCreatedAt());
    }

    @Test
    void getUserById_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(
                NoSuchElementException.class,
                () -> userService.getUserById(1L)
        );

        assertEquals("Пользователь не найден", ex.getMessage());
    }

    @Test
    void getAllUsers_shouldReturnUserList() {
        OffsetDateTime createdAt = OffsetDateTime.now();

        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("nikita");
        user1.setPasswordHash("hash");
        user1.setCreatedAt(createdAt);

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("admin");
        user2.setPasswordHash("hash");
        user2.setCreatedAt(createdAt);

        UserResponse response1 = new UserResponse(1L, "nikita", createdAt);
        UserResponse response2 = new UserResponse(2L, "admin", createdAt);

        when(userRepository.findAllByOrderByIdAsc()).thenReturn(List.of(user1, user2));
        when(userMapper.toResponse(user1)).thenReturn(response1);
        when(userMapper.toResponse(user2)).thenReturn(response2);

        List<UserResponse> responses = userService.getAllUsers();

        assertEquals(2, responses.size());
        assertEquals("nikita", responses.get(0).getUsername());
        assertEquals("admin", responses.get(1).getUsername());
    }

    @Test
    void deleteCurrentUser_shouldDeleteUser_whenPasswordIsValid() {
        User user = new User();
        user.setId(1L);
        user.setUsername("nikita");
        user.setPasswordHash("encoded-password");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("12345678", "encoded-password")).thenReturn(true);

        userService.deleteCurrentUser(1L, "12345678");

        verify(userRepository).delete(user);
    }

    @Test
    void deleteCurrentUser_shouldThrowUnauthorized_whenPasswordIsWrong() {
        User user = new User();
        user.setId(1L);
        user.setUsername("nikita");
        user.setPasswordHash("encoded-password");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpass", "encoded-password")).thenReturn(false);

        UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> userService.deleteCurrentUser(1L, "wrongpass")
        );

        assertEquals("Неверный пароль", ex.getMessage());

        verify(userRepository, never()).delete(any(User.class));
    }
}