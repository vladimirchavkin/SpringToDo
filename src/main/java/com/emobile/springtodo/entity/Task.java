package com.emobile.springtodo.entity;

import com.emobile.springtodo.entity.enumeration.CompletionStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    private Long id;

    private String title;

    private String description;

    private CompletionStatus isCompleted;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

}
