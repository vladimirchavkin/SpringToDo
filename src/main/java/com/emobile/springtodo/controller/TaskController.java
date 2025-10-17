package com.emobile.springtodo.controller;

import com.emobile.springtodo.controller.response.CustomResponse;
import com.emobile.springtodo.entity.dto.TaskRequest;
import com.emobile.springtodo.entity.dto.TaskResponse;
import com.emobile.springtodo.entity.enumeration.CompletionStatus;
import com.emobile.springtodo.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController implements SwaggerTaskController{

    private final TaskService taskService;

    @PostMapping
    public CustomResponse<TaskResponse> create(
            @RequestBody final TaskRequest taskRequest
    ) {
        log.info("Request to controller for create Task with DTO: {}", taskRequest);
        TaskResponse taskResponse = taskService.create(taskRequest);
        log.info("Response from controller for create Task with DTO: {}", taskResponse);
        return CustomResponse.success(taskResponse);
    }

    @GetMapping("/{id}")
    public CustomResponse<TaskResponse> findById(
            @PathVariable Long id
    ) {
        log.info("Request to find Task by id: {}", id);
        TaskResponse taskResponse = taskService.findById(id);
        log.info("Task found with id: {}", taskResponse.id());
        return CustomResponse.success(taskResponse);
    }

    @GetMapping("/all")
    public CustomResponse<Page<TaskResponse>> findAll(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer offset
    ) {
        log.info("Request to find all Tasks with page: {}, size: {}", page, offset);
        Page<TaskResponse> taskResponse = taskService.findAll(page, offset);
        log.info("Found {} tasks", taskResponse.getTotalElements());
        return CustomResponse.success(taskResponse);
    }

    @GetMapping("/by-is-completed")
    public CustomResponse<Page<TaskResponse>> findAllByIsCompleted(
            @RequestParam CompletionStatus isCompleted,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer offset
    ) {
        log.info("Request to find Tasks by isCompleted: {}", isCompleted);
        Page<TaskResponse> taskResponse = taskService.findAllByIsCompleted(isCompleted, page, offset);
        log.info("Found {} tasks by isCompleted", taskResponse.getTotalElements());
        return CustomResponse.success(taskResponse);
    }

    @PutMapping("/{id}")
    public CustomResponse<TaskResponse> update(
            @RequestBody TaskRequest taskRequest,
            @PathVariable Long id
    ) {
        log.info("Request to update Task by id: {}", id);
        TaskResponse taskResponse = taskService.update(taskRequest, id);
        log.info("Task updated with id: {}", taskResponse.id());
        return CustomResponse.success(taskResponse);
    }

    @DeleteMapping("/{id}")
    public CustomResponse<TaskResponse> delete(
            @PathVariable Long id
    ) {
        log.info("Request to delete Task by id: {}", id);
        TaskResponse taskResponse = taskService.delete(id);
        log.info("Task deleted with id: {}", taskResponse.id());
        return CustomResponse.success(taskResponse);
    }

}