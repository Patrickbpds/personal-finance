package com.patrick.personalfinance.application.mapper;

import com.patrick.personalfinance.application.dto.responses.UserResponseDto;
import com.patrick.personalfinance.domain.entity.User;
import com.patrick.personalfinance.security.dto.request.CreateUserRequest;

public final class UserMapper {

    private UserMapper() {}

    public static UserResponseDto toResponseDto(User user) {
        if (user == null) {
            return null;
        }

        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhoto(),
                user.getRegistrationDate(),
                user.getInactivationDate(),
                user.getUpdatedAt()
        );
    }

    public static User fromCreateRequest(CreateUserRequest dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        user.setPhoto(dto.photo());
        return user;
    }
}
