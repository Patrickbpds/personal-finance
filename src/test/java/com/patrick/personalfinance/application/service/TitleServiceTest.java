package com.patrick.personalfinance.application.service;

import com.patrick.personalfinance.application.dto.requests.TitleRequestDto;
import com.patrick.personalfinance.application.dto.responses.TitleResponseDto;
import com.patrick.personalfinance.application.port.CurrentUserProvider;
import com.patrick.personalfinance.domain.entity.CenterCost;
import com.patrick.personalfinance.domain.entity.Title;
import com.patrick.personalfinance.domain.entity.User;
import com.patrick.personalfinance.domain.enums.TitleType;
import com.patrick.personalfinance.domain.exceptions.ResourceBadRequestException;
import com.patrick.personalfinance.domain.exceptions.ResourceNotFoundException;
import com.patrick.personalfinance.domain.repository.CenterCostRepository;
import com.patrick.personalfinance.domain.repository.TitleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TitleServiceTest {

    @Mock
    private TitleRepository titleRepository;

    @Mock
    private CenterCostRepository centerCostRepository;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private TitleService titleService;

    @Test
    void acceptsDuplicatedCenterCostIds() {
        User user = user();
        CenterCost centerCost = centerCost(UUID.randomUUID(), user);
        UUID duplicatedId = centerCost.getId();

        when(currentUserProvider.getCurrentUser()).thenReturn(user);
        when(centerCostRepository.findAllById(any())).thenReturn(List.of(centerCost));
        when(titleRepository.save(any(Title.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TitleResponseDto response = titleService.create(dto(List.of(duplicatedId, duplicatedId)));

        assertThat(response.centerCosts()).hasSize(1);
        assertThat(response.centerCosts().getFirst().id()).isEqualTo(duplicatedId);
    }

    @Test
    void acceptsNullCenterCostIdsWithoutQuerying() {
        when(currentUserProvider.getCurrentUser()).thenReturn(user());
        when(titleRepository.save(any(Title.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TitleResponseDto response = titleService.create(dto(null));

        assertThat(response.centerCosts()).isEmpty();
        verifyNoInteractions(centerCostRepository);
    }

    @Test
    void rejectsCenterCostIdThatDoesNotExist() {
        when(currentUserProvider.getCurrentUser()).thenReturn(user());
        when(centerCostRepository.findAllById(any())).thenReturn(List.of());

        assertThatThrownBy(() -> titleService.create(dto(List.of(UUID.randomUUID()))))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Center costs not found");
    }

    @Test
    void rejectsCenterCostOwnedByAnotherUser() {
        User owner = user();
        User anotherUser = user();
        CenterCost anotherUsersCenterCost = centerCost(UUID.randomUUID(), anotherUser);

        when(currentUserProvider.getCurrentUser()).thenReturn(owner);
        when(centerCostRepository.findAllById(any())).thenReturn(List.of(anotherUsersCenterCost));

        assertThatThrownBy(() -> titleService.create(dto(List.of(anotherUsersCenterCost.getId()))))
                .isInstanceOf(ResourceBadRequestException.class)
                .hasMessageContaining("do not belong to the authenticated user");
    }

    private TitleRequestDto dto(List<UUID> centerCostIds) {
        return new TitleRequestDto(
                "Salary",
                TitleType.RECEIVABLE,
                centerCostIds,
                new BigDecimal("1000.00"),
                null,
                LocalDateTime.now().plusDays(30),
                null,
                null
        );
    }

    private User user() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Test User");
        user.setEmail(UUID.randomUUID() + "@example.com");
        user.setPassword("encoded-password");
        return user;
    }

    private CenterCost centerCost(UUID id, User user) {
        CenterCost centerCost = new CenterCost();
        centerCost.setId(id);
        centerCost.setDescription("Home");
        centerCost.setUser(user);
        return centerCost;
    }
}
