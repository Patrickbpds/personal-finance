package com.patrick.personalfinance.mapper;

import com.patrick.personalfinance.models.dto.requests.UserRequestDto;
import com.patrick.personalfinance.models.dto.responses.UserResponseDto;
import com.patrick.personalfinance.models.entity.User;
import com.patrick.personalfinance.security.request.CreateUserRequest;
import com.patrick.personalfinance.security.request.UpdateUserRequest;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class UserMapper {

    private UserMapper() {}

    public static UserResponseDto toResponseDto(User user) {
        if (user == null){
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

    public static User toEntity(UserRequestDto dto) {
        return mapBasicFields(dto == null ? null : dto.name(),
                dto == null ? null : dto.email(),
                dto == null ? null : dto.password(),
                dto == null ? null : dto.photo());
    }

    public static void updateEntityFromDto(UserRequestDto dto, User user) {
        if (dto == null || user == null) {
            return;
        }

       Optional.ofNullable(dto.name()).ifPresent(user::setName);
       Optional.ofNullable(dto.email()).ifPresent(user::setEmail);
       Optional.ofNullable(dto.password()).ifPresent(user::setPassword);
       Optional.ofNullable(dto.photo()).ifPresent(user::setPhoto);
    }

    public static List<UserResponseDto> toResponseDtoList(List<User> users) {
        if (users == null) {
            return List.of();
        }

        return users.stream()
                .filter(Objects::nonNull)
                .map(UserMapper::toResponseDto)
                .toList();
    }

    public static User fromCreateRequest(CreateUserRequest dto) {
        return mapBasicFields(dto == null ? null : dto.name(),
                dto == null ? null : dto.email(),
                dto == null ? null : dto.password(),
                dto == null ? null : dto.photo());
    }

    public static void updateEntityFromUpdateRequest(UpdateUserRequest dto, User user) {
        if (dto == null || user == null) {
            return;
        }

        Optional.ofNullable(dto.name()).ifPresent(user::setName);
        Optional.ofNullable(dto.email()).ifPresent(user::setEmail);
        Optional.ofNullable(dto.photo()).ifPresent(user::setPhoto);
        // Senha será tratada no serviço
    }

    private static User mapBasicFields(String name, String email, String password, String photo) {
        if (name == null && email == null && password == null && photo == null) {
            return null;
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhoto(photo);
        return user;
    }
}
