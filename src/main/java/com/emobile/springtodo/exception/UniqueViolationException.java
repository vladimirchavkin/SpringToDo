package com.emobile.springtodo.exception;

public class UniqueViolationException extends ApplicationException {

    public UniqueViolationException(final String message, final String errorCode) {
        super(message, errorCode);
    }

}
