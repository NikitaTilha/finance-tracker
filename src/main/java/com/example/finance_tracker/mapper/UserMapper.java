package com.example.finance_tracker.mapper;

import com.example.finance_tracker.dto.user.UserResponse;
import com.example.finance_tracker.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);
}