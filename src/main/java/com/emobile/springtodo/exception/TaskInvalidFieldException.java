package com.emobile.springtodo.exception;

public class TaskInvalidFieldException extends ApplicationException {

    public TaskInvalidFieldException(final String message, final String errorCode) {
        super(message, errorCode);
    }
}
