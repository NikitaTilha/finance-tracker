package com.example.finance_tracker.controller;

import com.example.finance_tracker.dto.user.CreateUserRequest;
import com.example.finance_tracker.dto.user.DeleteMeRequest;
import com.example.finance_tracker.dto.user.UserResponse;
import com.example.finance_tracker.exception.UnauthorizedException;
import com.example.finance_tracker.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserResponse getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @PostMapping("/me/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMe(@Valid @RequestBody DeleteMeRequest request,
                         HttpSession session) {
        Long currentUserId = (Long) session.getAttribute("userId");

        if (currentUserId == null) {
            throw new UnauthorizedException("Требуется авторизация");
        }

        userService.deleteCurrentUser(currentUserId, request.getPassword());
        session.invalidate();
    }
}