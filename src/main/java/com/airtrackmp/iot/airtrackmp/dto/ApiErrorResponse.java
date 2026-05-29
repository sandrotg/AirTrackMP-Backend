package com.airtrackmp.iot.airtrackmp.dto;

import org.springframework.http.HttpStatus;

import java.time.Instant;

public record ApiErrorResponse(
        int status,
        String error,
        String message,
        Instant timestamp,
        String path
) {
    public static ApiErrorResponse of(HttpStatus httpStatus, String message, String path) {
        return new ApiErrorResponse(
                httpStatus.value(),
                httpStatus.name(),
                message,
                Instant.now(),
                path
        );
    }
}
