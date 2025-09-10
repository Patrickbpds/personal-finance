package com.patrick.personalfinance.service;

import com.patrick.personalfinance.application.dto.requests.CenterCostRequestDto;
import com.patrick.personalfinance.application.dto.responses.CenterCostResponseDto;
import com.patrick.personalfinance.application.mapper.CenterCostMapper;
import com.patrick.personalfinance.domain.entity.CenterCost;
import com.patrick.personalfinance.domain.entity.User;
import com.patrick.personalfinance.domain.exceptions.ResourceNotFoundException;
import com.patrick.personalfinance.domain.repository.CenterCostRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
@AllArgsConstructor
public class CenterCostService implements CrudService<CenterCostRequestDto, CenterCostResponseDto> {

    private final CenterCostRepository centerCostRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CenterCostResponseDto> getAll() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<CenterCost> list = centerCostRepository.findByUser(user);
        return CenterCostMapper.toResponseDtoList(list);
    }

    @Override
    @Transactional(readOnly = true)
    public CenterCostResponseDto getById(UUID id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<CenterCost> optCenterCost = centerCostRepository.findByIdAndUser(id, user);

        if (optCenterCost.isEmpty()) {
            throw new ResourceNotFoundException("Center cost not found with id: " + id);
        }

        return CenterCostMapper.toResponseDto(optCenterCost.get());
    }

    @Override
    public CenterCostResponseDto create(CenterCostRequestDto dto) {
        CenterCost centerCost = CenterCostMapper.toEntity(dto);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        centerCost.setUser(user);
        centerCost.setId(null);

        centerCost = centerCostRepository.save(centerCost);
        return CenterCostMapper.toResponseDto(centerCost);
    }

    @Override
    public CenterCostResponseDto update(UUID id, CenterCostRequestDto dto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
