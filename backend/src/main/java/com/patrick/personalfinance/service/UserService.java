package com.patrick.personalfinance.service;

import com.patrick.personalfinance.application.mapper.UserMapper;
import com.patrick.personalfinance.application.dto.requests.UserRequestDto;
import com.patrick.personalfinance.application.dto.responses.UserResponseDto;
import com.patrick.personalfinance.domain.entity.User;
import com.patrick.personalfinance.domain.exceptions.ResourceBadRequestException;
import com.patrick.personalfinance.domain.exceptions.ResourceNotFoundException;
import com.patrick.personalfinance.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
@RequiredArgsConstructor
public class UserService implements CrudService<UserRequestDto, UserResponseDto> {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAll() {
        List<User> users = userRepository.findAll();
        return UserMapper.toResponseDtoList(users);
    }

    @Override
    @Transactional(readOnly = true)
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
        Optional<User> optionalUser = userRepository.findByEmail(dto.email());
        if (optionalUser.isPresent()) {
            throw new ResourceBadRequestException(
                    "User with email " + dto.email() + " already exists.");
        }
        User user = UserMapper.toEntity(dto);
        user.setId(null); // Ensure the ID is null for creation
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user = userRepository.save(user);
        return UserMapper.toResponseDto(user);
    }

    @Override
    public UserResponseDto update(UUID id, UserRequestDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + id));

        Optional<User> optionalUser = userRepository.findByEmail(dto.email());
            if (optionalUser.isPresent() && !optionalUser.get().getId().equals(id)) {
                throw new ResourceBadRequestException(
                        "User with email " + dto.email() + " already exists.");
            }

        UserMapper.updateEntityFromDto(dto, user);

        if (dto.password() != null && !dto.password().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.password()));
        }

        user = userRepository.save(user);
        return UserMapper.toResponseDto(user);
    }

    @Override
    public void delete(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + id));
        // Soft delete
        user.inactivate();

        userRepository.save(user);
    }
}
