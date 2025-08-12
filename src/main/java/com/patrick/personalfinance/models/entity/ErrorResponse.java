package com.patrick.personalfinance.models.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public final class ErrorResponse {

    private String DataTime;
    private String Status;
    private String Title;
    private String Detail;
}
