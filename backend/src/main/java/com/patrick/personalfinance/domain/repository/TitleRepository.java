package com.patrick.personalfinance.domain.repository;

import com.patrick.personalfinance.domain.entity.Title;
import com.patrick.personalfinance.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TitleRepository extends JpaRepository<Title, UUID> {

    List<Title> findByUser(User user);

    Optional<Title> findByIdAndUser(UUID id, User user);

    @Query("SELECT t FROM Title t WHERE t.user = :user AND t.expirationDate BETWEEN :startDate AND :endDate")
    List<Title> findByUserAndExpirationDateBetween(
            @Param("user") User user,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT t FROM Title t WHERE t.user = :user AND t.referenceDate BETWEEN :startDate AND :endDate")
    List<Title> findByUserAndReferenceDateBetween(
            @Param("user") User user,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

}
