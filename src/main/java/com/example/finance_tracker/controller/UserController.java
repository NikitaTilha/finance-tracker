package com.example.finance_tracker.controller;

import com.example.finance_tracker.config.CurrentUserId;
import com.example.finance_tracker.dto.user.CreateUserRequest;
import com.example.finance_tracker.dto.user.DeleteMeRequest;
import com.example.finance_tracker.dto.user.UserResponse;
import com.example.finance_tracker.service.SessionService;
import com.example.finance_tracker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final SessionService sessionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserResponse getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @PostMapping("/me/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMe(
            @CurrentUserId Long currentUserId,
            @Valid @RequestBody DeleteMeRequest request
    ) {
        userService.deleteCurrentUser(currentUserId, request.getPassword());
        sessionService.invalidateCurrentSession();
    }
}