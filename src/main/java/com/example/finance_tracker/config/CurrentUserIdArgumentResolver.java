package com.example.finance_tracker.config;

import com.example.finance_tracker.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
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
            throwUnauthorized();
        }

        HttpSession session = request.getSession(false);

        if (session == null) {
            throwUnauthorized();
        }

        Object userId = session.getAttribute(USER_ID_SESSION_ATTRIBUTE);

        if (userId == null) {
            throwUnauthorized();
        }

        if (userId instanceof Long longUserId) {
            return longUserId;
        }

        if (userId instanceof Number numberUserId) {
            return numberUserId.longValue();
        }

        throwUnauthorized();

        return null;
    }

    private void throwUnauthorized() {
        throw new ApiException(
                HttpStatus.UNAUTHORIZED,
                "Пользователь не авторизован"
        );
    }
}