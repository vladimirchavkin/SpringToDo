package com.emobile.springtodo.exception.handler;

import com.emobile.springtodo.controller.response.CustomResponse;
import com.emobile.springtodo.exception.PageableIllegalArgumentException;
import com.emobile.springtodo.exception.TaskInvalidFieldException;
import com.emobile.springtodo.exception.TaskNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public CustomResponse<String> handleTaskInvalidFieldException(final TaskInvalidFieldException e) {
        log.error(e.getMessage());
        return CustomResponse.error(e.getMessage(), e.getErrorCode(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public CustomResponse<String> handleTaskNotFoundException(final TaskNotFoundException e) {
        log.error(e.getMessage());
        return CustomResponse.error(e.getMessage(), e.getErrorCode(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public CustomResponse<String> handlePageableIllegalArgumentException(final PageableIllegalArgumentException e) {
        log.error(e.getMessage());
        return CustomResponse.error(e.getMessage(), e.getErrorCode(), HttpStatus.BAD_REQUEST);
    }

}
