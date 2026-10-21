package com.patrick.personalfinance.presentation.controller;

import com.patrick.personalfinance.application.dto.requests.UpdateProfileRequest;
import com.patrick.personalfinance.application.dto.responses.UserResponseDto;
import com.patrick.personalfinance.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
@Tag(name = "User Self-Service", description = "Manage the authenticated user's own account")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get the authenticated user's profile")
    public ResponseEntity<UserResponseDto> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @PutMapping
    @Operation(summary = "Update the authenticated user's profile")
    public ResponseEntity<UserResponseDto> updateCurrentUser(
            @Valid @RequestBody UpdateProfileRequest dto) {
        return ResponseEntity.ok(userService.updateCurrentUser(dto));
    }

    @DeleteMapping
    @Operation(summary = "Deactivate the authenticated user's account")
    public ResponseEntity<Void> deactivateCurrentUser() {
        userService.deactivateCurrentUser();
        return ResponseEntity.noContent().build();
    }
}
