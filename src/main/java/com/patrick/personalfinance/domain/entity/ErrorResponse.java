package com.patrick.personalfinance.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public final class ErrorResponse {

    private String DataTime;
    private String Status;
    private String Title;
    private String Detail;
}
