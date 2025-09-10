package com.emobile.springtodo.service.impl;

import com.emobile.springtodo.entity.Task;
import com.emobile.springtodo.entity.dto.TaskRequest;
import com.emobile.springtodo.entity.dto.TaskResponse;
import com.emobile.springtodo.entity.enumeration.CompletionStatus;
import com.emobile.springtodo.entity.enumeration.ExceptionMessage;
import com.emobile.springtodo.exception.TaskNotFoundException;
import com.emobile.springtodo.mapper.TaskMapper;
import com.emobile.springtodo.repository.TaskRepository;
import com.emobile.springtodo.service.TaskService;
import com.emobile.springtodo.validator.TaskRequestValidator;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskMapper taskMapper;

    private final TaskRepository taskRepository;

    private final TaskRequestValidator<TaskRequest> taskRequestValidator;

    private final MeterRegistry meterRegistry;

    @Override
    @Transactional
    @CachePut(value = "tasks", key = "#result.id")
    public TaskResponse create(final TaskRequest taskRequest) {
        taskRequestValidator.validate(taskRequest);

        log.info("REST request to save Task : {}", taskRequest);

        final Task taskToCreate = taskMapper.fromRequestToEntity(taskRequest);
        log.info("TaskRequest has been successfully mapped for creation method. Task entity: {}", taskToCreate);

        final Task createdTask = taskRepository.save(taskToCreate);
        log.info("Task has been successfully created. Task entity: {}", createdTask);

        Counter.builder("tasks.created").register(meterRegistry).increment();

        return taskMapper.fromEntityToResponse(createdTask);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "tasks", key = "#id")
    public TaskResponse findById(final Long id) {
        log.info("REST request to get Task by id: {}", id);

        final Task task = taskRepository.findById(id).orElseThrow(() ->
                new TaskNotFoundException(
                        ExceptionMessage.ENTITY_NOT_FOUND_BY_ID.getMessage(id),
                        ExceptionMessage.ENTITY_NOT_FOUND_BY_ID.getErrorCode())
        );
        log.info("Task by id has been successfully found. Task entity: {}", task);

        return taskMapper.fromEntityToResponse(task);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "tasks", key = "'all_page_' + #page + '_offset_' + #offset")
    public Page<TaskResponse> findAll(final Integer page, final Integer offset) {
        log.info("REST request to get all Tasks by page: {}", page);
        final PageRequest pageRequest = PageRequest.of(page, offset);

        final Page<Task> taskPage = taskRepository.findAll(pageRequest);
        log.info("Tasks has been successfully found. Task entities: {}", taskPage);

        return taskPage.map(taskMapper::fromEntityToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "tasks", key = "'completed_' + #isCompleted + '_page_' + #page + '_offset_' + #offset")
    public Page<TaskResponse> findAllByIsCompleted(final CompletionStatus isCompleted, final Integer page, final Integer offset) {
        log.info("REST request to get all Tasks by isCompleted: {}", isCompleted);
        final PageRequest pageRequest = PageRequest.of(page, offset);

        final Page<Task> taskPage = taskRepository.findAllByIsCompleted(isCompleted, pageRequest);
        log.info("Tasks by isCompleted has been successfully found. Task entities: {}", taskPage);

        return taskPage.map(taskMapper::fromEntityToResponse);
    }

    @Override
    @Transactional
    @CachePut(value = "tasks", key = "#result.id")
    public TaskResponse update(final TaskRequest taskRequest, final Long id) {

        taskRequestValidator.validate(taskRequest);

        log.info("REST request to update Task by id: {}", id);
        final Task taskToUpdate = taskRepository.findById(id).orElseThrow(() ->
                new TaskNotFoundException(
                        ExceptionMessage.ENTITY_NOT_FOUND_BY_ID.getMessage(id),
                        ExceptionMessage.ENTITY_NOT_FOUND_BY_ID.getErrorCode())
        );
        log.info("Task to update has been successfully found. Task entity: {}", taskToUpdate);

        final Task updatedTask = taskMapper.updateEntityFromRequest(taskToUpdate, taskRequest);
        log.info("Task to update has been successfully mapped. Task entity: {}", updatedTask);

        taskRepository.update(updatedTask);
        log.info("Task has been successfully saved. Task entity: {}", updatedTask);

        return taskMapper.fromEntityToResponse(updatedTask);
    }

    @Override
    @Transactional
    @CacheEvict(value = "tasks", key = "#id")
    public TaskResponse delete(final Long id) {
        log.info("REST request to delete Task by id: {}", id);

        final Task taskToDelete = taskRepository.findById(id).orElseThrow(() ->
                new TaskNotFoundException(
                        ExceptionMessage.ENTITY_NOT_FOUND_BY_ID.getMessage(id),
                        ExceptionMessage.ENTITY_NOT_FOUND_BY_ID.getErrorCode())
        );
        log.info("Task to delete has been successfully found. Task entity: {}", taskToDelete);

        taskRepository.delete(taskToDelete);
        log.info("Task has been successfully deleted. Task entity: {}", taskToDelete);

        return taskMapper.fromEntityToResponse(taskToDelete);
    }
}
