package com.patrick.personalfinance.presentation.controller;

import com.patrick.personalfinance.application.dto.requests.TitleRequestDto;
import com.patrick.personalfinance.application.dto.responses.TitleResponseDto;
import com.patrick.personalfinance.service.TitleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@CrossOrigin("*")
@RestController
@RequestMapping("/api/titles")
@Tag(name = "Title Management", description = "Financial title management APIs")
public class TitleController {

    private final TitleService titleService;

    @GetMapping
    @Operation(summary = "Get all titles for the authenticated user")
    public ResponseEntity<List<TitleResponseDto>> getAll() {
        return ResponseEntity.ok(titleService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get title by ID")
    public ResponseEntity<TitleResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(titleService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create new title")
    public ResponseEntity<TitleResponseDto> create(@Valid @RequestBody TitleRequestDto dto) {
        TitleResponseDto title = titleService.create(dto);
        return ResponseEntity.status(201).body(title);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update title")
    public ResponseEntity<TitleResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody TitleRequestDto dto) {
        TitleResponseDto title = titleService.update(id, dto);
        return ResponseEntity.ok(title);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete title")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        titleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-expiration-date")
    @Operation(summary = "Get titles by expiration date range")
    public ResponseEntity<List<TitleResponseDto>> getByExpirationDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(titleService.getByExpirationDateRange(startDate, endDate));
    }

    @GetMapping("/by-reference-date")
    @Operation(summary = "Get titles by reference date range")
    public ResponseEntity<List<TitleResponseDto>> getByReferenceDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(titleService.getByReferenceDateRange(startDate, endDate));
    }
}
