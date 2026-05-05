package com.example.finance_tracker.service;

import com.example.finance_tracker.exception.UnauthorizedException;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserServiceImpl implements CurrentUserService {

    @Override
    public Long getCurrentUserId(HttpSession session) {
        Object userId = session.getAttribute("userId");

        if (userId == null) {
            throw new UnauthorizedException("Пользователь не авторизован");
        }

        return (Long) userId;
    }
}