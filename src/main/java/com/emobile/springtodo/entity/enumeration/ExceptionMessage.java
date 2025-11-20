package com.emobile.springtodo.entity.enumeration;

import lombok.Getter;

import java.util.IllegalFormatException;

@Getter
public enum ExceptionMessage {

    ENTITY_NOT_FOUND_BY_ID("Entity not found by id: %s", "404"),
    ENTITY_IS_NULL("Entity is null", "404"),

    ID_IS_NULL("Task id is null", "400"),
    PAGEABLE_IS_NULL("Pageable is null", "400"),
    TITLE_IS_NULL_OR_EMPTY("Task title is null or empty: %s", "400"),
    DESCRIPTION_IS_NULL_OR_EMPTY("Task description is null or empty: %s", "400"),
    CREATED_AT_IS_NULL("Task createdAt is null", "400"),
    COMPLETED_IS_NULL("Task isCompleted is null", "400"),

    UNIQUE_VIOLATION("Unique violation: %s", "409");

    private final String message;
    private final String errorCode;

    ExceptionMessage(final String message, final String errorCode) {
        this.message = message;
        this.errorCode = errorCode;
    }

    public String getMessage(Object... params) {
        try {
            return String.format(message, params);
        } catch (IllegalFormatException e) {
            return message;
        }
    }
}