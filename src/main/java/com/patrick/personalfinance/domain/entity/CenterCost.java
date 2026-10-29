package com.patrick.personalfinance.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Entity
@Table(name = "center_costs", schema = "finance_tables")
public class CenterCost {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "idcentercost")
    @ToString.Include
    private UUID id;

    @Column(nullable = false)
    @ToString.Include
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
