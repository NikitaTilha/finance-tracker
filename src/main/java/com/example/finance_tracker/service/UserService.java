package com.example.finance_tracker.service;

import com.example.finance_tracker.dto.user.CreateUserRequest;
import com.example.finance_tracker.dto.user.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long userId);

    void deleteCurrentUser(Long currentUserId, String rawPassword);
}