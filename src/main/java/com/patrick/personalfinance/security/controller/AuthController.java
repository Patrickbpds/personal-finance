package com.patrick.personalfinance.security.controller;


import com.patrick.personalfinance.security.dto.request.CreateUserRequest;
import com.patrick.personalfinance.security.dto.request.LoginRequest;
import com.patrick.personalfinance.security.dto.request.UpdateUserRequest;
import com.patrick.personalfinance.security.dto.response.TokenResponse;
import com.patrick.personalfinance.security.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        TokenResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Register a new user")
    public ResponseEntity<String> register(@Valid @RequestBody CreateUserRequest createUserRequest) {
        authService.register(createUserRequest);
        return ResponseEntity.status(201).body("User registered successfully");
    }

    @PutMapping("/update")
    @Operation(summary = "Update user", description = "Update user information")
    public ResponseEntity<String> update(@Valid @RequestBody UpdateUserRequest updateUserRequest) {
        authService.update(updateUserRequest);
        return ResponseEntity.ok("User updated successfully");
    }
}
