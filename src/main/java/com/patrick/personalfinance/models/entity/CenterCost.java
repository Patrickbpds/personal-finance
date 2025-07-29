package com.patrick.personalfinance.models.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.List;
import java.util.UUID;

@With
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "center_costs", schema = "finance_tables")
public class CenterCost {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "idCenterCost")
    private UUID id;

    @Column(nullable = false)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String observation;

    @ManyToOne
    @JoinColumn(name = "idUser")
    private User user;

    @ManyToMany(mappedBy = "center_costs")
    @JsonBackReference
    private List<Title> titles;
}
