package com.emobile.springtodo.exception.handler;

import com.emobile.springtodo.controller.response.ApiResponse;
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
    public ApiResponse<String> handleTaskInvalidFieldException(final TaskInvalidFieldException e) {
        log.error(e.getMessage());
        return ApiResponse.error(e.getMessage(), e.getErrorCode(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ApiResponse<String> handleTaskNotFoundException(final TaskNotFoundException e) {
        log.error(e.getMessage());
        return ApiResponse.error(e.getMessage(), e.getErrorCode(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ApiResponse<String> handlePageableIllegalArgumentException(final PageableIllegalArgumentException e) {
        log.error(e.getMessage());
        return ApiResponse.error(e.getMessage(), e.getErrorCode(), HttpStatus.BAD_REQUEST);
    }

}
