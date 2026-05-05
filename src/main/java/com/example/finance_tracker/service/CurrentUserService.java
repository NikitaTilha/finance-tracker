package com.example.finance_tracker.service;

import jakarta.servlet.http.HttpSession;

public interface CurrentUserService {

    Long getCurrentUserId(HttpSession session);
}