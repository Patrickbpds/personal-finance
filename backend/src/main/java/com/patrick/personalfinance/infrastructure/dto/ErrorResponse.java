package com.patrick.personalfinance.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public final class ErrorResponse {

    @JsonProperty("timestamp")
    private String dateTime;

    @JsonProperty("status")
    private String status;

    @JsonProperty("error")
    private String title;

    @JsonProperty("message")
    private String detail;

    @JsonProperty("path")
    private String path;

    public ErrorResponse(String dateTime, String status, String title, String detail) {
        this.dateTime = dateTime;
        this.status = status;
        this.title = title;
        this.detail = detail;
    }
}
