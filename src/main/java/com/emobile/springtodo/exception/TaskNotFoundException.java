package com.emobile.springtodo.exception;

public class TaskNotFoundException extends ApplicationException {

    public TaskNotFoundException(final String message, final String errorCode) {
        super(message, errorCode);
    }
}
