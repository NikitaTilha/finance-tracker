package com.example.finance_tracker.controller;

import com.example.finance_tracker.dto.auth.LoginRequest;
import com.example.finance_tracker.dto.auth.LoginResponse;
import com.example.finance_tracker.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}