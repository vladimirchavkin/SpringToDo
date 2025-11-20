package com.emobile.springtodo.entity.enumeration;

import lombok.Getter;

@Getter
public enum CompletionStatus {

    COMPLETED("COMPLETED"),
    NOT_COMPLETED("NOT_COMPLETED");

    private final String status;

    CompletionStatus(String status) {
        this.status = status;
    }

}
