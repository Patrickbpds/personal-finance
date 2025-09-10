package com.patrick.personalfinance.presentation.controller;

import com.patrick.personalfinance.application.dto.responses.DashboardResponseDto;
import com.patrick.personalfinance.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin("*")
@Tag(name = "Dashboard", description = "Dashboard and financial overview APIs")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/cash-flow")
    @Operation(summary = "Get cash flow data", description = "Get financial overview including payable and receivable titles within a date range")
    public ResponseEntity<DashboardResponseDto> getCashFlow(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        DashboardResponseDto cashFlow = dashboardService.getCashFlow(startDate, endDate);
        return ResponseEntity.ok(cashFlow);
    }
}
