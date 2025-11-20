package com.emobile.springtodo.entity.dto;

import com.emobile.springtodo.entity.enumeration.CompletionStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

public record TaskResponse(

        Long id,

        String title,

        String description,

        CompletionStatus isCompleted,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) implements Serializable {
}
