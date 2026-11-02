package com.patrick.personalfinance.service;

import com.patrick.personalfinance.application.dto.requests.CenterCostRequestDto;
import com.patrick.personalfinance.application.dto.responses.CenterCostResponseDto;
import com.patrick.personalfinance.application.mapper.CenterCostMapper;
import com.patrick.personalfinance.application.port.CurrentUserProvider;
import com.patrick.personalfinance.domain.entity.CenterCost;
import com.patrick.personalfinance.domain.entity.User;
import com.patrick.personalfinance.domain.exceptions.ResourceNotFoundException;
import com.patrick.personalfinance.domain.repository.CenterCostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
@RequiredArgsConstructor
public class CenterCostService implements CrudService<CenterCostRequestDto, CenterCostResponseDto> {

    private final CenterCostRepository centerCostRepository;
    private final CurrentUserProvider currentUserProvider;

    @Override
    @Transactional(readOnly = true)
    public Page<CenterCostResponseDto> getAll(Pageable pageable) {
        User user = currentUserProvider.getCurrentUser();
        return centerCostRepository.findByUser(user, withDefaultSort(pageable))
                .map(CenterCostMapper::toResponseDto);
    }

    private Pageable withDefaultSort(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) {
            return PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.ASC, "description")
            );
        }
        return pageable;
    }

    @Override
    @Transactional(readOnly = true)
    public CenterCostResponseDto getById(UUID id) {
        User user = currentUserProvider.getCurrentUser();
        Optional<CenterCost> optCenterCost = centerCostRepository.findByIdAndUser(id, user);

        if (optCenterCost.isEmpty()) {
            throw new ResourceNotFoundException("Center cost not found with id: " + id);
        }

        return CenterCostMapper.toResponseDto(optCenterCost.get());
    }

    @Override
    public CenterCostResponseDto create(CenterCostRequestDto dto) {
        CenterCost centerCost = CenterCostMapper.toEntity(dto);
        User user = currentUserProvider.getCurrentUser();

        centerCost.setUser(user);
        centerCost.setId(null);

        centerCost = centerCostRepository.save(centerCost);
        return CenterCostMapper.toResponseDto(centerCost);
    }

    @Override
    public CenterCostResponseDto update(UUID id, CenterCostRequestDto dto) {
        User user = currentUserProvider.getCurrentUser();
        CenterCost centerCost = centerCostRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Center cost not found with id: " + id));

        CenterCostMapper.updateEntityFromDto(dto, centerCost);

        centerCost = centerCostRepository.save(centerCost);
        return CenterCostMapper.toResponseDto(centerCost);
    }

    @Override
    public void delete(UUID id) {
        getById(id);
        centerCostRepository.deleteById(id);
    }
}
