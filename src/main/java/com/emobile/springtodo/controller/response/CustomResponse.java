package com.emobile.springtodo.controller.response;

import org.springframework.http.HttpStatus;

public record CustomResponse<T>(
        T data,
        String errorCode,
        String errorMessage,
        HttpStatus status
        ) {
    public static <T> CustomResponse<T> success(
            final T data
    ) {
        return new CustomResponse<>(data, null, null, HttpStatus.OK);
    }

    public static <T> CustomResponse<T> error(
            final String errorMessage,
            final String errorCode,
            final HttpStatus status
            ) {
        return new CustomResponse<>(null, errorMessage, errorCode, status);
    }
}