package com.emobile.springtodo.validator;

import com.emobile.springtodo.entity.dto.TaskRequest;
import com.emobile.springtodo.entity.enumeration.ExceptionMessage;
import com.emobile.springtodo.exception.TaskInvalidFieldException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskRequestValidator<T extends TaskRequest> {

    public void validate(final T request) {

        if (request.title() == null || request.title().trim().isEmpty()) {
            throw new TaskInvalidFieldException(
                    ExceptionMessage.TITLE_IS_NULL_OR_EMPTY.getMessage("null"),
                    ExceptionMessage.TITLE_IS_NULL_OR_EMPTY.getErrorCode()
            );
        }

        if (request.description() == null || request.description().trim().isEmpty()) {
            throw new TaskInvalidFieldException(
                    ExceptionMessage.DESCRIPTION_IS_NULL_OR_EMPTY.getMessage("null"),
                    ExceptionMessage.DESCRIPTION_IS_NULL_OR_EMPTY.getErrorCode()
            );
        }
    }

}
