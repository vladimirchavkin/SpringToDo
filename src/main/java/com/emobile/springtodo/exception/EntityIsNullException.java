package com.emobile.springtodo.exception;

public class EntityIsNullException extends ApplicationException {
    public EntityIsNullException(final String message, final String errorCode) {
        super(message, errorCode);
    }
}
