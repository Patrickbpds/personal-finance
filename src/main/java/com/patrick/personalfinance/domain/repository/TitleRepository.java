package com.patrick.personalfinance.domain.repository;

import com.patrick.personalfinance.domain.entity.Title;
import com.patrick.personalfinance.domain.entity.User;
import com.patrick.personalfinance.domain.enums.TitleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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

    @Query("""
            SELECT t.type AS type, SUM(t.value) AS total
            FROM Title t
            WHERE t.user = :user
              AND t.expirationDate BETWEEN :startDate AND :endDate
            GROUP BY t.type
            """)
    List<TitleTypeTotal> sumValueByType(
            @Param("user") User user,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("""
            SELECT SUM(t.value)
            FROM Title t
            WHERE t.user = :user
              AND t.expirationDate BETWEEN :startDate AND :endDate
              AND t.paymentDate IS NOT NULL
            """)
    BigDecimal sumPaidValue(
            @Param("user") User user,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("""
            SELECT SUM(t.value)
            FROM Title t
            WHERE t.user = :user
              AND t.expirationDate BETWEEN :startDate AND :endDate
              AND t.paymentDate IS NULL
              AND t.expirationDate < :now
            """)
    BigDecimal sumOverdueValue(
            @Param("user") User user,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("now") LocalDateTime now
    );

    @Query("""
            SELECT SUM(t.value)
            FROM Title t
            WHERE t.user = :user
              AND t.expirationDate BETWEEN :startDate AND :endDate
              AND t.paymentDate IS NULL
              AND t.expirationDate >= :now
            """)
    BigDecimal sumPendingValue(
            @Param("user") User user,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("now") LocalDateTime now
    );

    interface TitleTypeTotal {

        TitleType getType();

        BigDecimal getTotal();
    }

}
