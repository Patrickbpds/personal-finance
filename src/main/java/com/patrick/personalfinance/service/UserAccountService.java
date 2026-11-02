package com.patrick.personalfinance.service;

import com.patrick.personalfinance.application.dto.requests.UpdateProfileRequest;
import com.patrick.personalfinance.application.dto.responses.UserResponseDto;
import com.patrick.personalfinance.application.mapper.UserMapper;
import com.patrick.personalfinance.application.port.CurrentUserProvider;
import com.patrick.personalfinance.domain.entity.User;
import com.patrick.personalfinance.domain.exceptions.EmailAlreadyExistsException;
import com.patrick.personalfinance.domain.exceptions.InvalidCredentialsException;
import com.patrick.personalfinance.domain.repository.UserRepository;
import com.patrick.personalfinance.security.dto.request.CreateUserRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class UserAccountService {

    private static final String EMAIL_ALREADY_REGISTERED = "Email already registered";

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CurrentUserProvider currentUserProvider;

    public void register(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(EMAIL_ALREADY_REGISTERED);
        }

        User user = UserMapper.fromCreateRequest(request);
        user.setPassword(passwordEncoder.encode(request.password()));

        try {
            userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException ex) {
            throw new EmailAlreadyExistsException(EMAIL_ALREADY_REGISTERED);
        }

        log.info("User {} successfully registered", request.email());
    }

    @Transactional(readOnly = true)
    public UserResponseDto getCurrentUser() {
        return UserMapper.toResponseDto(currentUserProvider.getCurrentUser());
    }

    public UserResponseDto updateCurrentUser(UpdateProfileRequest dto) {
        User user = currentUserProvider.getCurrentUser();
        boolean passwordChangeRequested = isPasswordChangeRequested(dto);

        if (passwordChangeRequested) {
            verifyCurrentPassword(user, dto.currentPassword());
        }

        user.setName(dto.name());
        user.setPhoto(dto.photo());

        if (passwordChangeRequested) {
            encodePassword(user, dto.newPassword());
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

    private void encodePassword(User user, String rawPassword) {
        if (rawPassword != null && !rawPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(rawPassword));
        }
    }

    private void verifyCurrentPassword(User user, String currentPassword) {
        if (currentPassword == null || !passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }
    }
}
