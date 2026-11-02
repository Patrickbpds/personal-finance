package com.patrick.personalfinance.security.service;

import com.patrick.personalfinance.application.mapper.UserMapper;
import com.patrick.personalfinance.domain.entity.User;
import com.patrick.personalfinance.domain.exceptions.EmailAlreadyExistsException;
import com.patrick.personalfinance.domain.repository.UserRepository;
import com.patrick.personalfinance.security.dto.request.CreateUserRequest;
import com.patrick.personalfinance.security.dto.request.LoginRequest;
import com.patrick.personalfinance.security.dto.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public TokenResponse login(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.email(),
                            loginRequest.password()
                    )
            );

            String token = jwtService.generateToken(loginRequest.email());
            log.info("Successful login for {}", loginRequest.email());
            return new TokenResponse(token);
        } catch (AuthenticationException ex) {
            log.warn("Login failure for {}: {}", loginRequest.email(), ex.getMessage());
            throw new BadCredentialsException("Invalid credentials");
        }
    }

    public void register(CreateUserRequest createUserRequest) {
        if (userRepository.findByEmail(createUserRequest.email()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already registered");
        }

        User user = UserMapper.fromCreateRequest(createUserRequest);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        log.info("User {} successfully registered", createUserRequest.email());
    }
}
