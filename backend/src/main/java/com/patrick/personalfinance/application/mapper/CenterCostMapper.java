package com.patrick.personalfinance.application.mapper;

import com.patrick.personalfinance.application.dto.requests.CenterCostRequestDto;
import com.patrick.personalfinance.application.dto.responses.CenterCostResponseDto;
import com.patrick.personalfinance.domain.entity.CenterCost;

import java.util.List;
import java.util.Objects;

public final class CenterCostMapper {
    private CenterCostMapper() {}

    public static CenterCostResponseDto toResponseDto(CenterCost centerCost) {
        if (centerCost == null) {
            return null;
        }

        return new CenterCostResponseDto(
                centerCost.getId(),
                centerCost.getDescription(),
                centerCost.getObservation()
        );
    }

    public static CenterCost toEntity(CenterCostRequestDto dto) {
        if (dto == null) {
            return null;
        }

        CenterCost centerCost = new CenterCost();
        centerCost.setDescription(dto.description());
        centerCost.setObservation(dto.observation());
        return centerCost;
    }

    public static void updateEntityFromDto(CenterCostRequestDto dto, CenterCost centerCost) {
        if (dto == null || centerCost == null) {
            return;
        }

        if (dto.description() != null) {
            centerCost.setDescription(dto.description());
        }
        if (dto.observation() != null) {
            centerCost.setObservation(dto.observation());
        }
    }

    public static List<CenterCostResponseDto> toResponseDtoList(List<CenterCost> centerCosts) {
        if (centerCosts == null) {
            return List.of();
        }

        return centerCosts.stream()
                .filter(Objects::nonNull)
                .map(CenterCostMapper::toResponseDto)
                .toList();
    }
}
