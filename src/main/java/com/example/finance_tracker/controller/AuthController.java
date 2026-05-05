package com.example.finance_tracker.controller;

import com.example.finance_tracker.config.CurrentUserId;
import com.example.finance_tracker.dto.auth.LoginRequest;
import com.example.finance_tracker.dto.auth.LoginResponse;
import com.example.finance_tracker.dto.user.UserResponse;
import com.example.finance_tracker.service.AuthService;
import com.example.finance_tracker.service.SessionService;
import com.example.finance_tracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final SessionService sessionService;

    public AuthController(
            AuthService authService,
            UserService userService,
            SessionService sessionService
    ) {
        this.authService = authService;
        this.userService = userService;
        this.sessionService = sessionService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);

        sessionService.saveCurrentUser(response.getId(), response.getUsername());

        return response;
    }

    @PostMapping("/logout")
    public void logout() {
        sessionService.invalidateCurrentSession();
    }

    @GetMapping("/me")
    public UserResponse me(@CurrentUserId Long userId) {
        return userService.getUserById(userId);
    }
}