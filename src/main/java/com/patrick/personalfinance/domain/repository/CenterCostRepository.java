package com.patrick.personalfinance.domain.repository;

import com.patrick.personalfinance.domain.entity.CenterCost;
import com.patrick.personalfinance.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CenterCostRepository extends JpaRepository<CenterCost, UUID> {

    Page<CenterCost> findByUser(User user, Pageable pageable);
    Optional<CenterCost> findByIdAndUser(UUID id, User user);
}
