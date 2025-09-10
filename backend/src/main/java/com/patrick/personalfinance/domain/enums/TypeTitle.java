package com.patrick.personalfinance.domain.enums;

import lombok.Getter;

@Getter
public enum TypeTitle {
    RECEIVABLE("Receivable"),
    PAYABLE("Payable");

    private String value;

    TypeTitle(String value) {
        this.value = value;
    }

}
