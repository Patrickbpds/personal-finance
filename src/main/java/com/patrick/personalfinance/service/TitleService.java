package com.patrick.personalfinance.service;

import com.patrick.personalfinance.application.dto.requests.TitleRequestDto;
import com.patrick.personalfinance.application.dto.responses.TitleResponseDto;
import com.patrick.personalfinance.application.mapper.TitleMapper;
import com.patrick.personalfinance.application.port.CurrentUserProvider;
import com.patrick.personalfinance.domain.entity.CenterCost;
import com.patrick.personalfinance.domain.entity.Title;
import com.patrick.personalfinance.domain.entity.User;
import com.patrick.personalfinance.domain.exceptions.ResourceBadRequestException;
import com.patrick.personalfinance.domain.exceptions.ResourceNotFoundException;
import com.patrick.personalfinance.domain.repository.CenterCostRepository;
import com.patrick.personalfinance.domain.repository.TitleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class TitleService implements CrudService<TitleRequestDto, TitleResponseDto> {

    private final TitleRepository titleRepository;
    private final CenterCostRepository centerCostRepository;
    private final CurrentUserProvider currentUserProvider;

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
        User user = getAuthenticatedUser();

        Title title = Title.of(
                user,
                dto.description(),
                dto.type(),
                dto.value(),
                dto.expirationDate(),
                dto.referenceDate(),
                dto.paymentDate(),
                dto.observation()
        );

        title.setCenterCosts(associateCenterCosts(dto.centerCostIds(), user));

        title = titleRepository.save(title);
        return TitleMapper.toResponseDto(title);
    }

    @Override
    public TitleResponseDto update(UUID id, TitleRequestDto dto) {
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

    private User getAuthenticatedUser() {
        return currentUserProvider.getCurrentUser();
    }

    private List<CenterCost> associateCenterCosts(List<UUID> centerCostIds, User user) {
        if (centerCostIds == null || centerCostIds.isEmpty()) {
            return List.of();
        }

        Set<UUID> requestedIds = new LinkedHashSet<>(centerCostIds);
        List<CenterCost> found = centerCostRepository.findAllById(requestedIds);

        Set<UUID> foundIds = found.stream()
                .map(CenterCost::getId)
                .collect(Collectors.toSet());

        Set<UUID> missingIds = new LinkedHashSet<>(requestedIds);
        missingIds.removeAll(foundIds);
        if (!missingIds.isEmpty()) {
            throw new ResourceNotFoundException("Center costs not found: " + missingIds);
        }

        List<CenterCost> owned = found.stream()
                .filter(centerCost -> centerCost.getUser().getId().equals(user.getId()))
                .toList();

        if (owned.size() != requestedIds.size()) {
            throw new ResourceBadRequestException("Center costs do not belong to the authenticated user");
        }

        return owned;
    }
}
