package com.patrick.personalfinance.service;

import com.patrick.personalfinance.application.dto.requests.UpdateProfileRequest;
import com.patrick.personalfinance.application.dto.responses.UserResponseDto;
import com.patrick.personalfinance.application.mapper.UserMapper;
import com.patrick.personalfinance.application.port.CurrentUserProvider;
import com.patrick.personalfinance.domain.entity.User;
import com.patrick.personalfinance.domain.exceptions.InvalidCredentialsException;
import com.patrick.personalfinance.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CurrentUserProvider currentUserProvider;

    @Transactional(readOnly = true)
    public UserResponseDto getCurrentUser() {
        return UserMapper.toResponseDto(currentUserProvider.getCurrentUser());
    }

    
    public UserResponseDto updateCurrentUser(UpdateProfileRequest dto) {
        User user = currentUserProvider.getCurrentUser();

        user.setName(dto.name());
        user.setPhoto(dto.photo());

        if (isPasswordChangeRequested(dto)) {
            verifyCurrentPassword(user, dto.currentPassword());
            user.setPassword(passwordEncoder.encode(dto.newPassword()));
        }

        User saved = userRepository.save(user);
        return UserMapper.toResponseDto(saved);
    }

    public void deactivateCurrentUser() {
        User user = currentUserProvider.getCurrentUser();
        user.inactivate();
        userRepository.save(user);
    }

    private boolean isPasswordChangeRequested(UpdateProfileRequest dto) {
        return dto.newPassword() != null && !dto.newPassword().isBlank();
    }

    private void verifyCurrentPassword(User user, String currentPassword) {
        if (currentPassword == null || !passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }
    }
}
