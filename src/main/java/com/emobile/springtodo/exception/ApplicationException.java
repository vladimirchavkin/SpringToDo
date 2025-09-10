package com.emobile.springtodo.exception;

import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {

    private final String errorCode;

    public ApplicationException(final String message, final String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
