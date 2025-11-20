package com.emobile.springtodo.entity.dto;

import com.emobile.springtodo.entity.enumeration.CompletionStatus;
import jakarta.validation.constraints.*;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Validated
public record TaskRequest(

        @NotBlank(message = "'title' must not be null or empty.")
        @Size(min = 1, max = 50, message = "'title' should be between 1 to 50.")
        String title,

        @NotBlank(message = "'description' must not be null or empty.")
        @Size(min = 1, max = 500, message = "'description' should be between 1 to 500.")
        String description,

        @NotNull(message = "'isCompleted' must not be null.")
        CompletionStatus isCompleted,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}
