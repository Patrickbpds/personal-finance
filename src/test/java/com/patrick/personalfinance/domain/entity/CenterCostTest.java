package com.patrick.personalfinance.domain.entity;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CenterCostTest {

    @Test
    void usesIdentityEqualityInsteadOfFieldEquality() {
        UUID id = UUID.randomUUID();
        CenterCost centerCost = centerCostWithId(id);
        CenterCost other = centerCostWithId(id);

        assertThat(centerCost).isEqualTo(centerCost);
        assertThat(centerCost).isNotEqualTo(other);
    }

    @Test
    void toStringIncludesOnlyIdentityFields() {
        CenterCost centerCost = centerCostWithId(UUID.randomUUID());
        centerCost.setDescription("Home");
        centerCost.setObservation("Rent and utilities");
        centerCost.setTitles(List.of());

        assertThat(centerCost.toString())
                .contains("Home")
                .doesNotContain("Rent and utilities");
    }

    private CenterCost centerCostWithId(UUID id) {
        CenterCost centerCost = new CenterCost();
        centerCost.setId(id);
        return centerCost;
    }
}
