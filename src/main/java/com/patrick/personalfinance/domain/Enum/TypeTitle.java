package com.patrick.personalfinance.domain.Enum;

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
