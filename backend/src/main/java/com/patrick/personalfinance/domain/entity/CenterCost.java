package com.patrick.personalfinance.domain.entity;

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
    @Column(name = "idcentercost")
    private UUID id;

    @Column(nullable = false)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String observation;

    @ManyToOne
    @JoinColumn(name = "iduser")
    private User user;

    @ManyToMany(mappedBy = "centerCosts")
    @JsonBackReference
    private List<Title> titles;
}
