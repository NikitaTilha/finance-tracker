package com.example.finance_tracker.service;

import com.example.finance_tracker.exception.ApiException;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class SessionServiceImpl implements SessionService {

    private static final String USER_ID_SESSION_ATTRIBUTE = "userId";
    private static final String USERNAME_SESSION_ATTRIBUTE = "username";

    @Override
    public void saveCurrentUser(Long userId, String username) {
        ServletRequestAttributes attributes = getServletRequestAttributes();

        HttpSession session = attributes.getRequest().getSession(true);
        session.setAttribute(USER_ID_SESSION_ATTRIBUTE, userId);
        session.setAttribute(USERNAME_SESSION_ATTRIBUTE, username);
    }

    @Override
    public void invalidateCurrentSession() {
        ServletRequestAttributes attributes = getServletRequestAttributes();

        HttpSession session = attributes.getRequest().getSession(false);

        if (session != null) {
            session.invalidate();
        }
    }

    private ServletRequestAttributes getServletRequestAttributes() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            throw new ApiException(
                    HttpStatus.UNAUTHORIZED,
                    "Пользователь не авторизован"
            );
        }

        return attributes;
    }
}