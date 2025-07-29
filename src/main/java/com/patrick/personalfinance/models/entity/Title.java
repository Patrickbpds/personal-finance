package com.patrick.personalfinance.models.entity;

import com.patrick.personalfinance.models.Enum.TypeTitle;
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
    @Column(name = "idTitle")
    private UUID id;

    @Column(nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "idUser")
    private User user;

    @Enumerated
    private TypeTitle type;

    @ManyToMany
    @JoinTable(
            name = "title_center_costs",
            joinColumns = @JoinColumn(name = "idTitle"),
            inverseJoinColumns = @JoinColumn(name = "idCenterCost")
    )
    private List<CenterCost> centerCosts;

    @Column(nullable = false)
    private Double value;

    private LocalDateTime registrationDate;

    private LocalDateTime referenceDate;

    private LocalDateTime expirationDate;

    private LocalDateTime paymentDate;

    @Column(columnDefinition = "TEXT")
    private String observation;
}
