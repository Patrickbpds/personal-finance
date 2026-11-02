package com.patrick.personalfinance.domain.entity;

import com.patrick.personalfinance.domain.enums.TitleType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TitleTest {

    @Test
    void padsValueToTwoDecimals() {
        Title title = new Title();

        title.setValue(new BigDecimal("100"));

        assertThat(title.getValue()).isEqualTo(new BigDecimal("100.00"));
        assertThat(title.getValue().scale()).isEqualTo(2);
    }

    @Test
    void keepsTwoDecimalPlacesAsIs() {
        Title title = new Title();

        title.setValue(new BigDecimal("100.50"));

        assertThat(title.getValue()).isEqualTo(new BigDecimal("100.50"));
    }

    @Test
    void roundsHalfUpWhenMoreDecimalsAreProvided() {
        Title title = new Title();

        title.setValue(new BigDecimal("100.005"));

        assertThat(title.getValue()).isEqualTo(new BigDecimal("100.01"));
    }

    @Test
    void acceptsNullValue() {
        Title title = new Title();

        title.setValue(null);

        assertThat(title.getValue()).isNull();
    }

    @Test
    void usesIdentityEqualityInsteadOfFieldEquality() {
        UUID id = UUID.randomUUID();
        Title title = titleWithId(id);
        Title other = titleWithId(id);

        assertThat(title).isEqualTo(title);
        assertThat(title).isNotEqualTo(other);
    }

    @Test
    void toStringIncludesOnlyIdentityFields() {
        Title title = titleWithId(UUID.randomUUID());
        title.setDescription("Salary");
        title.setValue(new BigDecimal("1500.00"));
        title.setCenterCosts(List.of());
        title.setObservation("October");

        assertThat(title.toString())
                .contains("Salary")
                .contains("1500.00")
                .doesNotContain("October");
    }

    @Test
    void factorySetsTheOwnerAndTheRegistrationDate() {
        User owner = new User();
        LocalDateTime before = LocalDateTime.now();

        Title title = Title.of(
                owner,
                "Salary",
                TitleType.RECEIVABLE,
                new BigDecimal("1500.00"),
                LocalDateTime.now().plusDays(30),
                LocalDateTime.now(),
                null,
                "October"
        );

        assertThat(title.getUser()).isSameAs(owner);
        assertThat(title.getRegistrationDate()).isBetween(before, LocalDateTime.now());
        assertThat(title.getDescription()).isEqualTo("Salary");
        assertThat(title.getType()).isEqualTo(TitleType.RECEIVABLE);
        assertThat(title.getValue()).isEqualTo(new BigDecimal("1500.00"));
        assertThat(title.getObservation()).isEqualTo("October");
    }

    private Title titleWithId(UUID id) {
        Title title = new Title();
        title.setId(id);
        return title;
    }
}
