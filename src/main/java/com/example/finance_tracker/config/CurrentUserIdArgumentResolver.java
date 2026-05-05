package com.example.finance_tracker.config;

import com.example.finance_tracker.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CurrentUserIdArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String USER_ID_SESSION_ATTRIBUTE = "userId";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUserId.class)
                && Long.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        if (request == null) {
            throw new UnauthorizedException("Пользователь не авторизован");
        }

        HttpSession session = request.getSession(false);

        if (session == null) {
            throw new UnauthorizedException("Пользователь не авторизован");
        }

        Object userId = session.getAttribute(USER_ID_SESSION_ATTRIBUTE);

        if (userId == null) {
            throw new UnauthorizedException("Пользователь не авторизован");
        }

        if (userId instanceof Long longUserId) {
            return longUserId;
        }

        if (userId instanceof Number numberUserId) {
            return numberUserId.longValue();
        }

        throw new UnauthorizedException("Пользователь не авторизован");
    }
}