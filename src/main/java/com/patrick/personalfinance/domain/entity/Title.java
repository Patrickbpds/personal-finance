package com.patrick.personalfinance.domain.entity;

import com.patrick.personalfinance.domain.enums.TitleType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Entity
@Table(name = "title", schema = "finance_tables")
public class Title {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "idtitle")
    @ToString.Include
    private UUID id;

    @Column(nullable = false)
    @ToString.Include
    private String description;

    @ManyToOne
    @JoinColumn(name = "iduser")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TitleType type;

    @ManyToMany
    @JoinTable(
            name = "title_center_costs",
            schema = "finance_tables",
            joinColumns = @JoinColumn(name = "idtitle"),
            inverseJoinColumns = @JoinColumn(name = "idcentercost")
    )
    private List<CenterCost> centerCosts;

    @Column(nullable = false, precision = 15, scale = 2)
    @ToString.Include
    private BigDecimal value;

    @Column(name = "registrationdate")
    private LocalDateTime registrationDate;

    @Column(name = "referencedate")
    private LocalDateTime referenceDate;

    @Column(name = "expirationdate")
    private LocalDateTime expirationDate;

    @Column(name = "paymentdate")
    private LocalDateTime paymentDate;

    @Column(columnDefinition = "TEXT")
    private String observation;

    public void setValue(BigDecimal value) {
        this.value = value == null ? null : value.setScale(2, RoundingMode.HALF_UP);
    }
}
