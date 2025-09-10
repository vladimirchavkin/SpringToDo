package com.emobile.springtodo.entity.enumeration;

import lombok.Getter;

@Getter
public enum ExceptionMessage {

    ENTITY_NOT_FOUND_BY_ID("Entity not found by id: %s", "404"),
    ENTITY_IS_NULL("Entity is null", "404"),

    ID_IS_NULL("Received id is null", "400"),
    PAGEABLE_IS_NULL("Received pageable is null", "400"),
    TITLE_IS_NULL_OR_EMPTY("Invalid title: %s", "400"),
    DESCRIPTION_IS_NULL_OR_EMPTY("Invalid description: %s", "400"),
    CREATED_AT_IS_NULL("Invalid created at", "400"),
    COMPLETED_IS_NULL("Invalid completed", "400"),

    UNIQUE_VIOLATION("Unique violation: %s", "409");

    private final String message;
    private final String errorCode;

    ExceptionMessage(final String message, final String errorCode) {
        this.message = message;
        this.errorCode = errorCode;
    }

    public String getMessage(Object... params) {
        return String.format(message, params);
    }
}