package com.patrick.personalfinance.service;

import com.patrick.personalfinance.mapper.UserMapper;
import com.patrick.personalfinance.models.dto.requests.UserRequestDto;
import com.patrick.personalfinance.models.dto.responses.UserResponseDto;
import com.patrick.personalfinance.models.entity.User;
import com.patrick.personalfinance.models.exceptions.ResourceNotFoundException;
import com.patrick.personalfinance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements CrudService<UserRequestDto, UserResponseDto> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<UserResponseDto> getAll() {
        List<User> users = userRepository.findAll();
        return UserMapper.toResponseDtoList(users);
    }

    @Override
    public UserResponseDto getById(UUID id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return UserMapper.toResponseDto(user.get());
        } else {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
    }

    @Override
    public UserResponseDto create(UserRequestDto dto) {
        User user = UserMapper.toEntity(dto);
        // ENCODE PASSWORD
        user.setId(null); // Ensure the ID is null for creation
        user = userRepository.save(user);
        return UserMapper.toResponseDto(user);
    }

    @Override
    public UserResponseDto update(UUID id, UserRequestDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + id));
        UserMapper.updateEntityFromDto(dto, user);
        // ENCODE PASSWORD
        user = userRepository.save(user);
        return UserMapper.toResponseDto(user);
    }

    @Override
    public void delete(UUID id) {
        getById(id);
        userRepository.deleteById(id);
    }
}
