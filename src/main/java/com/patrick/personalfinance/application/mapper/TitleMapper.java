package com.patrick.personalfinance.application.mapper;

import com.patrick.personalfinance.application.dto.requests.TitleRequestDto;
import com.patrick.personalfinance.application.dto.responses.TitleResponseDto;
import com.patrick.personalfinance.domain.entity.Title;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class TitleMapper {
    private TitleMapper() {}

    public static TitleResponseDto toResponseDto(Title title) {
        return toResponseDto(title, LocalDateTime.now());
    }

    public static TitleResponseDto toResponseDto(Title title, LocalDateTime reference) {
        if (title == null) {
            return null;
        }

        return new TitleResponseDto(
                title.getId(),
                title.getDescription(),
                title.getType(),
                title.paymentStatus(reference),
                title.getCenterCosts() != null ?
                        CenterCostMapper.toResponseDtoList(title.getCenterCosts()) :
                        List.of(),
                title.getValue(),
                title.getRegistrationDate(),
                title.getReferenceDate(),
                title.getExpirationDate(),
                title.getPaymentDate(),
                title.getObservation()
        );
    }

    public static void updateEntityFromDto(TitleRequestDto dto, Title title) {
        if (dto == null || title == null) {
            return;
        }

        Optional.ofNullable(dto.description()).ifPresent(title::setDescription);
        Optional.ofNullable(dto.type()).ifPresent(title::setType);
        Optional.ofNullable(dto.value()).ifPresent(title::setValue);
        Optional.ofNullable(dto.referenceDate()).ifPresent(title::setReferenceDate);
        Optional.ofNullable(dto.expirationDate()).ifPresent(title::setExpirationDate);
        Optional.ofNullable(dto.paymentDate()).ifPresent(title::setPaymentDate);
        Optional.ofNullable(dto.observation()).ifPresent(title::setObservation);
    }

    public static List<TitleResponseDto> toResponseDtoList(List<Title> titles) {
        return toResponseDtoList(titles, LocalDateTime.now());
    }

    public static List<TitleResponseDto> toResponseDtoList(List<Title> titles, LocalDateTime reference) {
        if (titles == null) {
            return List.of();
        }

        return titles.stream()
                .filter(Objects::nonNull)
                .map(title -> toResponseDto(title, reference))
                .toList();
    }
}
