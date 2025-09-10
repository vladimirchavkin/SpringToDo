package com.emobile.springtodo.controller;

import com.emobile.springtodo.controller.response.ApiResponse;
import com.emobile.springtodo.entity.dto.TaskRequest;
import com.emobile.springtodo.entity.dto.TaskResponse;
import com.emobile.springtodo.entity.enumeration.CompletionStatus;
import com.emobile.springtodo.service.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;


    @PostMapping
    public ApiResponse<TaskResponse> create(
            @Valid @RequestBody final TaskRequest taskRequest
    ) {
        log.info("Request to controller for create Task with DTO: {}", taskRequest);
        TaskResponse taskResponse = taskService.create(taskRequest);
        log.info("Response from controller for create Task with DTO: {}", taskResponse);
        return ApiResponse.success(taskResponse);
    }

    @GetMapping("/{id}")
    public ApiResponse<TaskResponse> findById(
            @PathVariable @Positive Long id
    ) {
        log.info("Request to find Task by id: {}", id);
        TaskResponse taskResponse = taskService.findById(id);
        log.info("Task found with id: {}", taskResponse.id());
        return ApiResponse.success(taskResponse);
    }

    @GetMapping("/all")
    public ApiResponse<Page<TaskResponse>> findAll(
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer page,
            @RequestParam(defaultValue = "10") @Positive Integer offset
    ) {
        log.info("Request to find all Tasks with page: {}, size: {}", page, offset);
        Page<TaskResponse> taskResponse = taskService.findAll(page, offset);
        log.info("Found {} tasks", taskResponse.getTotalElements());
        return ApiResponse.success(taskResponse);
    }

    @GetMapping("/by-is-completed")
    public ApiResponse<Page<TaskResponse>> findAllByIsCompleted(
            @RequestParam CompletionStatus isCompleted,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer page,
            @RequestParam(defaultValue = "10") @Positive Integer offset
    ) {
        log.info("Request to find Tasks by isCompleted: {}", isCompleted);
        Page<TaskResponse> taskResponse = taskService.findAllByIsCompleted(isCompleted, page, offset);
        log.info("Found {} tasks by isCompleted", taskResponse.getTotalElements());
        return ApiResponse.success(taskResponse);
    }

    @PutMapping("/{id}")
    public ApiResponse<TaskResponse> update(
            @RequestBody TaskRequest taskRequest,
            @PathVariable @Positive Long id
    ) {
        log.info("Request to update Task by id: {}", id);
        TaskResponse taskResponse = taskService.update(taskRequest, id);
        log.info("Task updated with id: {}", taskResponse.id());
        return ApiResponse.success(taskResponse);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<TaskResponse> delete(
            @PathVariable @Positive Long id
    ) {
        log.info("Request to delete Task by id: {}", id);
        TaskResponse taskResponse = taskService.delete(id);
        log.info("Task deleted with id: {}", taskResponse.id());
        return ApiResponse.success(taskResponse);
    }

}