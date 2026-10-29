package com.patrick.personalfinance.domain.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

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
}
