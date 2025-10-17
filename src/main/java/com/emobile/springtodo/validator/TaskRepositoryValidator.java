package com.emobile.springtodo.validator;

import com.emobile.springtodo.entity.Task;
import com.emobile.springtodo.entity.enumeration.ExceptionMessage;
import com.emobile.springtodo.exception.EntityIsNullException;
import com.emobile.springtodo.exception.TaskInvalidFieldException;

public class TaskRepositoryValidator {

    public static void validateTask(Task task) {
        if (task == null) {
            throw new EntityIsNullException(
                    ExceptionMessage.ENTITY_IS_NULL.getMessage(),
                    ExceptionMessage.ENTITY_IS_NULL.getErrorCode());
        }
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            throw new TaskInvalidFieldException(
                    ExceptionMessage.TITLE_IS_NULL_OR_EMPTY.getMessage(task.getTitle()),
                    ExceptionMessage.TITLE_IS_NULL_OR_EMPTY.getErrorCode());
        }
        if (task.getCreatedAt() == null) {
            throw new TaskInvalidFieldException(
                    ExceptionMessage.CREATED_AT_IS_NULL.getMessage(),
                    ExceptionMessage.CREATED_AT_IS_NULL.getErrorCode());
        }
    }
}
