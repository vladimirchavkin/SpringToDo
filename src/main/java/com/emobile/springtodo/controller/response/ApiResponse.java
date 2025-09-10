package com.emobile.springtodo.controller.response;

import org.springframework.http.HttpStatus;

public record ApiResponse<T>(
        T data,
        String errorCode,
        String errorMessage,
        HttpStatus status
        ) {
    public static <T> ApiResponse<T> success(
            final T data
    ) {
        return new ApiResponse<>(data, null, null, HttpStatus.OK);
    }

    public static <T> ApiResponse<T> error(
            final String errorMessage,
            final String errorCode,
            final HttpStatus status
            ) {
        return new ApiResponse<>(null, errorMessage, errorCode, status);
    }
}