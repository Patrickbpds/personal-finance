package com.patrick.personalfinance.service;

import com.patrick.personalfinance.application.dto.requests.TitleRequestDto;
import com.patrick.personalfinance.application.dto.responses.TitleResponseDto;
import com.patrick.personalfinance.application.mapper.TitleMapper;
import com.patrick.personalfinance.domain.entity.CenterCost;
import com.patrick.personalfinance.domain.entity.Title;
import com.patrick.personalfinance.domain.entity.User;
import com.patrick.personalfinance.domain.exceptions.ResourceBadRequestException;
import com.patrick.personalfinance.domain.exceptions.ResourceNotFoundException;
import com.patrick.personalfinance.domain.repository.CenterCostRepository;
import com.patrick.personalfinance.domain.repository.TitleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
@RequiredArgsConstructor
public class TitleService implements CrudService<TitleRequestDto, TitleResponseDto> {

    private final TitleRepository titleRepository;
    private final CenterCostRepository centerCostRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TitleResponseDto> getAll() {
        User user = getAuthenticatedUser();
        List<Title> titles = titleRepository.findByUser(user);
        return TitleMapper.toResponseDtoList(titles);
    }

    @Override
    @Transactional(readOnly = true)
    public TitleResponseDto getById(UUID id) {
        User user = getAuthenticatedUser();
        Optional<Title> optTitle = titleRepository.findByIdAndUser(id, user);

        if (optTitle.isEmpty()) {
            throw new ResourceNotFoundException("Title not found with id: " + id);
        }

        return TitleMapper.toResponseDto(optTitle.get());
    }

    @Override
    public TitleResponseDto create(TitleRequestDto dto) {
        validateTitle(dto);

        Title title = TitleMapper.toEntity(dto);
        User user = getAuthenticatedUser();

        title.setUser(user);
        title.setId(null);
        title.setRegistrationDate(LocalDateTime.now());

        title.setCenterCosts(associateCenterCosts(dto.centerCostIds(), user));

        title = titleRepository.save(title);
        return TitleMapper.toResponseDto(title);
    }

    @Override
    public TitleResponseDto update(UUID id, TitleRequestDto dto) {
        validateTitle(dto);

        User user = getAuthenticatedUser();
        Title title = titleRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Title not found with id: " + id));

        TitleMapper.updateEntityFromDto(dto, title);

        if (dto.centerCostIds() != null) {
            title.setCenterCosts(associateCenterCosts(dto.centerCostIds(), user));
        }

        title = titleRepository.save(title);
        return TitleMapper.toResponseDto(title);
    }

    @Override
    public void delete(UUID id) {
        getById(id);
        titleRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<TitleResponseDto> getByExpirationDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        User user = getAuthenticatedUser();
        List<Title> titles = titleRepository.findByUserAndExpirationDateBetween(user, startDate, endDate);
        return TitleMapper.toResponseDtoList(titles);
    }

    @Transactional(readOnly = true)
    public List<TitleResponseDto> getByReferenceDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        User user = getAuthenticatedUser();
        List<Title> titles = titleRepository.findByUserAndReferenceDateBetween(user, startDate, endDate);
        return TitleMapper.toResponseDtoList(titles);
    }

    private void validateTitle(TitleRequestDto dto) {
        if (dto.type() == null ||
                dto.expirationDate() == null ||
                dto.value() == null ||
                dto.description() == null) {
            throw new ResourceBadRequestException("Type, Expiration Date, Value and Description are required fields.");
        }

        if (dto.value() <= 0) {
            throw new ResourceBadRequestException("Value must be greater than zero.");
        }
    }

    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private List<CenterCost> associateCenterCosts(List<UUID> centerCostIds, User user) {
        if (centerCostIds == null || centerCostIds.isEmpty()) {
            return List.of();
        }

        List<CenterCost> centerCosts = centerCostRepository.findAllById(centerCostIds)
                .stream()
                .filter(centerCost -> centerCost.getUser().getId().equals(user.getId()))
                .toList();

        if (centerCosts.size() != centerCostIds.size()) {
            throw new ResourceBadRequestException("Some center costs not found or don't belong to the user");
        }

        return centerCosts;
    }
}
