package com.patrick.personalfinance.presentation.controller;

import com.patrick.personalfinance.application.dto.requests.CenterCostRequestDto;
import com.patrick.personalfinance.application.dto.responses.CenterCostResponseDto;
import com.patrick.personalfinance.service.CenterCostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@CrossOrigin("*")
@RestController
@RequestMapping("/api/center-costs")
@Tag(name = "Center Cost Management", description = "Center cost management APIs")
public class CenterCostController {

    private final CenterCostService centerCostService;

    @GetMapping
    @Operation(summary = "Get all center costs for the authenticated user")
    public ResponseEntity<List<CenterCostResponseDto>> getAll() {
        return ResponseEntity.ok(centerCostService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get center cost by ID")
    public ResponseEntity<CenterCostResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(centerCostService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create new center cost")
    public ResponseEntity<CenterCostResponseDto> create(@Valid @RequestBody CenterCostRequestDto dto) {
        CenterCostResponseDto centerCost = centerCostService.create(dto);
        return ResponseEntity.status(201).body(centerCost);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update center cost")
    public ResponseEntity<CenterCostResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody CenterCostRequestDto dto) {
        CenterCostResponseDto centerCost = centerCostService.update(id, dto);
        return ResponseEntity.ok(centerCost);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete center cost")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        centerCostService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
