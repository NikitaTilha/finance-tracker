package com.example.finance_tracker.controller;

import com.example.finance_tracker.dto.auth.LoginRequest;
import com.example.finance_tracker.dto.auth.LoginResponse;
import com.example.finance_tracker.dto.user.UserResponse;
import com.example.finance_tracker.service.AuthService;
import com.example.finance_tracker.service.CurrentUserService;
import com.example.finance_tracker.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final CurrentUserService currentUserService;

    public AuthController(AuthService authService,
                          UserService userService,
                          CurrentUserService currentUserService) {
        this.authService = authService;
        this.userService = userService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        LoginResponse response = authService.login(request);

        session.setAttribute("userId", response.getId());
        session.setAttribute("username", response.getUsername());

        return response;
    }

    @PostMapping("/logout")
    public void logout(HttpSession session) {
        session.invalidate();
    }

    @GetMapping("/me")
    public UserResponse me(HttpSession session) {
        Long userId = currentUserService.getCurrentUserId(session);
        return userService.getUserById(userId);
    }
}