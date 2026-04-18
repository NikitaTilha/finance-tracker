package com.example.finance_tracker.mapper;

import com.example.finance_tracker.dto.user.UserResponse;
import com.example.finance_tracker.entity.User;

public class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getCreatedAt()
        );
    }
}