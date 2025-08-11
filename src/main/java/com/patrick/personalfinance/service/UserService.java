package com.patrick.personalfinance.service;

import com.patrick.personalfinance.models.dto.requests.UserRequestDto;
import com.patrick.personalfinance.models.dto.responses.UserResponseDto;

import java.util.List;
import java.util.UUID;

public class UserService implements CrudService<UserRequestDto, UserResponseDto> {

    @Override
    public List<UserResponseDto> getAll() {
        return List.of();
    }

    @Override
    public UserResponseDto getById(UUID id) {
        return null;
    }

    @Override
    public UserResponseDto create(UserRequestDto dto) {
        return null;
    }

    @Override
    public UserResponseDto update(UUID id, UserRequestDto dto) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }
}
