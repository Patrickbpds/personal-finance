package com.patrick.personalfinance.presentation.controller;

import com.patrick.personalfinance.application.dto.requests.CreateUserRequest;
import com.patrick.personalfinance.application.service.UserAccountService;
import com.patrick.personalfinance.security.dto.request.LoginRequest;
import com.patrick.personalfinance.security.dto.response.TokenResponse;
import com.patrick.personalfinance.security.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    private final AuthService authService;
    private final UserAccountService userAccountService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    public ResponseEntity<TokenResponse> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request) {
        TokenResponse response = authService.login(loginRequest, request.getRemoteAddr());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Register a new user")
    public ResponseEntity<String> register(@Valid @RequestBody CreateUserRequest createUserRequest) {
        userAccountService.register(createUserRequest);
        return ResponseEntity.status(201).body("User registered successfully");
    }
}
