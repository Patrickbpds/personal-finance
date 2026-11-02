package com.patrick.personalfinance.service;

import com.patrick.personalfinance.application.dto.requests.CenterCostRequestDto;
import com.patrick.personalfinance.application.dto.responses.CenterCostResponseDto;
import com.patrick.personalfinance.application.port.CurrentUserProvider;
import com.patrick.personalfinance.domain.entity.CenterCost;
import com.patrick.personalfinance.domain.entity.User;
import com.patrick.personalfinance.domain.exceptions.ResourceNotFoundException;
import com.patrick.personalfinance.domain.repository.CenterCostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CenterCostServiceTest {

    @Mock
    private CenterCostRepository centerCostRepository;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private CenterCostService centerCostService;

    @Test
    void listsOnlyTheCurrentUserCenterCostsWithDefaultSort() {
        User user = user();
        CenterCost centerCost = centerCost("Rent", user);
        Pageable requested = PageRequest.of(0, 10);

        when(currentUserProvider.getCurrentUser()).thenReturn(user);
        when(centerCostRepository.findByUser(any(User.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(centerCost)));

        Page<CenterCostResponseDto> result = centerCostService.getAll(requested);

        assertThat(result.getContent())
                .extracting(CenterCostResponseDto::description)
                .containsExactly("Rent");

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(centerCostRepository).findByUser(eq(user), captor.capture());
        assertThat(captor.getValue().getSort().getOrderFor("description"))
                .isEqualTo(Sort.Order.asc("description"));
    }

    @Test
    void keepsTheSortRequestedByTheClient() {
        User user = user();
        Pageable requested = PageRequest.of(1, 5, Sort.by(Sort.Direction.DESC, "id"));

        when(currentUserProvider.getCurrentUser()).thenReturn(user);
        when(centerCostRepository.findByUser(any(User.class), any(Pageable.class)))
                .thenReturn(Page.empty(requested));

        centerCostService.getAll(requested);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(centerCostRepository).findByUser(eq(user), captor.capture());
        assertThat(captor.getValue()).isEqualTo(requested);
    }

    @Test
    void looksUpByIdOnlyWithinTheCurrentUser() {
        User user = user();
        CenterCost centerCost = centerCost("Rent", user);

        when(currentUserProvider.getCurrentUser()).thenReturn(user);
        when(centerCostRepository.findByIdAndUser(centerCost.getId(), user)).thenReturn(Optional.of(centerCost));

        CenterCostResponseDto result = centerCostService.getById(centerCost.getId());

        assertThat(result.description()).isEqualTo("Rent");
        verify(centerCostRepository).findByIdAndUser(centerCost.getId(), user);
    }

    @Test
    void rejectsUnknownIdsWithoutTouchingOtherUsersData() {
        User user = user();
        UUID id = UUID.randomUUID();

        when(currentUserProvider.getCurrentUser()).thenReturn(user);
        when(centerCostRepository.findByIdAndUser(id, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> centerCostService.getById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Center cost not found with id");
    }

    @Test
    void assignsTheCurrentUserWhenCreating() {
        User user = user();

        when(currentUserProvider.getCurrentUser()).thenReturn(user);
        when(centerCostRepository.save(any(CenterCost.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CenterCostResponseDto result = centerCostService.create(
                new CenterCostRequestDto("Rent", "Monthly rent"));

        assertThat(result.description()).isEqualTo("Rent");
        ArgumentCaptor<CenterCost> captor = ArgumentCaptor.forClass(CenterCost.class);
        verify(centerCostRepository).save(captor.capture());
        assertThat(captor.getValue().getUser()).isSameAs(user);
        assertThat(captor.getValue().getId()).isNull();
    }

    @Test
    void refusesToDeleteACenterCostFromAnotherUser() {
        User user = user();
        UUID id = UUID.randomUUID();

        when(currentUserProvider.getCurrentUser()).thenReturn(user);
        when(centerCostRepository.findByIdAndUser(id, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> centerCostService.delete(id))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(centerCostRepository, never()).deleteById(any(UUID.class));
    }

    private CenterCost centerCost(String description, User user) {
        CenterCost centerCost = new CenterCost();
        centerCost.setId(UUID.randomUUID());
        centerCost.setDescription(description);
        centerCost.setUser(user);
        return centerCost;
    }

    private User user() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Test User");
        user.setEmail("center-cost@example.com");
        user.setPassword("encoded-password");
        return user;
    }
}
