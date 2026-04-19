package com.example.finance_tracker.controller;

import com.example.finance_tracker.dto.auth.LoginRequest;
import com.example.finance_tracker.dto.auth.LoginResponse;
import com.example.finance_tracker.dto.user.UserResponse;
import com.example.finance_tracker.exception.GlobalExceptionHandler;
import com.example.finance_tracker.exception.InvalidCredentialsException;
import com.example.finance_tracker.exception.UnauthorizedException;
import com.example.finance_tracker.service.AuthService;
import com.example.finance_tracker.service.CurrentUserService;
import com.example.finance_tracker.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserService userService;

    @MockBean
    private CurrentUserService currentUserService;

    @Test
    void login_shouldReturn200_whenCredentialsAreValid() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("nikita");
        request.setPassword("12345678");

        LoginResponse response = new LoginResponse(
                1L,
                "nikita",
                OffsetDateTime.now(),
                true
        );

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("nikita"))
                .andExpect(jsonPath("$.authenticated").value(true));
    }

    @Test
    void login_shouldReturn401_whenCredentialsAreInvalid() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("nikita");
        request.setPassword("wrongpass");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new InvalidCredentialsException("Неверный username или пароль"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Неверный username или пароль"));
    }

    @Test
    void me_shouldReturn200_whenUserIsAuthenticated() throws Exception {
        UserResponse response = new UserResponse(
                1L,
                "nikita",
                OffsetDateTime.now()
        );

        when(currentUserService.getCurrentUserId(any())).thenReturn(1L);
        when(userService.getUserById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("nikita"));
    }

    @Test
    void me_shouldReturn401_whenUserIsNotAuthenticated() throws Exception {
        when(currentUserService.getCurrentUserId(any()))
                .thenThrow(new UnauthorizedException("Пользователь не авторизован"));

        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Пользователь не авторизован"));
    }

    @Test
    void logout_shouldReturn200() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk());
    }
}