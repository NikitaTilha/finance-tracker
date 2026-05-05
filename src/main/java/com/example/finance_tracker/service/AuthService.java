package com.example.finance_tracker.service;

import com.example.finance_tracker.dto.auth.LoginRequest;
import com.example.finance_tracker.dto.auth.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}