package com.patrick.personalfinance.domain.entity;

import com.patrick.personalfinance.domain.enums.TypeTitle;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@With
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "title", schema = "finance_tables")
public class Title {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "idtitle")
    private UUID id;

    @Column(nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "iduser")
    private User user;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type")
    private TypeTitle type;

    @ManyToMany
    @JoinTable(
            name = "title_center_costs",
            schema = "finance_tables",
            joinColumns = @JoinColumn(name = "idtitle"),
            inverseJoinColumns = @JoinColumn(name = "idcentercost")
    )
    private List<CenterCost> centerCosts;

    @Column(nullable = false)
    private Double value;

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
}
