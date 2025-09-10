package com.emobile.springtodo.exception;

public class PageableIllegalArgumentException extends ApplicationException {

    public PageableIllegalArgumentException(final String message, final String errorCode) {
        super(message, errorCode);
    }
}
