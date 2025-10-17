package com.emobile.springtodo.service;

import com.emobile.springtodo.entity.Task;
import com.emobile.springtodo.entity.dto.TaskRequest;
import com.emobile.springtodo.entity.dto.TaskResponse;
import com.emobile.springtodo.entity.enumeration.CompletionStatus;
import com.emobile.springtodo.entity.enumeration.ExceptionMessage;
import com.emobile.springtodo.exception.TaskInvalidFieldException;
import com.emobile.springtodo.exception.TaskNotFoundException;
import com.emobile.springtodo.mapper.TaskMapper;
import com.emobile.springtodo.repository.JpaTaskRepository;
import com.emobile.springtodo.service.impl.TaskServiceImpl;
import com.emobile.springtodo.validator.TaskRequestValidator;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TaskServiceImplTest {

    @Mock
    private TaskMapper taskMapper;

//    @Mock
//    private TaskRepository taskRepository;

//    @Mock
//    private HibernateTaskRepository taskRepository;

    @Mock
    private JpaTaskRepository taskRepository;

    @Mock
    private TaskRequestValidator taskRequestValidator;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter counter;

    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp() {
        when(meterRegistry.counter("tasks.created")).thenReturn(counter);
        taskService = new TaskServiceImpl(taskMapper, taskRepository, taskRequestValidator, meterRegistry);
    }

    @Test
    @DisplayName("Создание задачи: выбрасывает исключение при невалидном запросе")
    void create_shouldThrowException_whenRequestInvalid() {
        // Arrange
        TaskRequest request = new TaskRequest(null, "Desc", CompletionStatus.NOT_COMPLETED, LocalDateTime.now(), null);
        doThrow(new TaskInvalidFieldException("Invalid title", "400")).when(taskRequestValidator).validate(request);

        // Act & Assert
        TaskInvalidFieldException exception = assertThrows(TaskInvalidFieldException.class, () -> taskService.create(request));
        assertEquals("Invalid title", exception.getMessage());
        verifyNoInteractions(taskRepository, taskMapper);
    }

    @Test
    @DisplayName("Поиск задачи по ID: возвращает задачу, если ID существует")
    void findById_shouldReturnTask_whenIdExists() {
        // Arrange
        Long id = 1L;
        Task task = new Task(id, "Title", "Desc", CompletionStatus.COMPLETED, LocalDateTime.now(), null);
        TaskResponse response = new TaskResponse(id, "Title", "Desc", CompletionStatus.COMPLETED, LocalDateTime.now(), null);

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(taskMapper.fromEntityToResponse(task)).thenReturn(response);

        // Act
        TaskResponse result = taskService.findById(id);

        // Assert
        assertEquals(response, result);
        verify(taskRepository).findById(id);
        verify(taskMapper).fromEntityToResponse(task);
    }

    @Test
    @DisplayName("Поиск задачи по ID: выбрасывает исключение, если задача не найдена")
    void findById_shouldThrowNotFound_whenIdNotExists() {
        // Arrange
        Long id = 1L;
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> taskService.findById(id));
        assertEquals(ExceptionMessage.ENTITY_NOT_FOUND_BY_ID.getMessage(id), exception.getMessage());
        verify(taskRepository).findById(id);
        verifyNoMoreInteractions(taskMapper);
    }

    @Test
    @DisplayName("Получение всех задач: возвращает страницу задач")
    void findAll_shouldReturnPagedTasks() {
        // Arrange
        Integer page = 0;
        Integer offset = 10;
        PageRequest pageRequest = PageRequest.of(page, offset);
        List<Task> tasks = List.of(new Task(1L, "Title1", "Desc1", CompletionStatus.NOT_COMPLETED, LocalDateTime.now(), null));
        Page<Task> taskPage = new PageImpl<>(tasks, pageRequest, 1);
        TaskResponse response = new TaskResponse(1L, "Title1", "Desc1", CompletionStatus.NOT_COMPLETED, LocalDateTime.now(), null);
        Page<TaskResponse> expectedPage = new PageImpl<>(List.of(response), pageRequest, 1);

        when(taskRepository.findAll(pageRequest)).thenReturn(taskPage);
        when(taskMapper.fromEntityToResponse(any(Task.class))).thenReturn(response);

        // Act
        Page<TaskResponse> result = taskService.findAll(page, offset);

        // Assert
        assertEquals(expectedPage.getTotalElements(), result.getTotalElements());
        assertEquals(expectedPage.getContent(), result.getContent());
        verify(taskRepository).findAll(pageRequest);
    }

    @Test
    @DisplayName("Получение задач по статусу завершения: возвращает страницу задач")
    void findAllByIsCompleted_shouldReturnPagedTasks() {
        // Arrange
        CompletionStatus status = CompletionStatus.COMPLETED;
        Integer page = 0;
        Integer offset = 10;
        PageRequest pageRequest = PageRequest.of(page, offset);
        List<Task> tasks = List.of(new Task(1L, "Title", "Desc", status, LocalDateTime.now(), null));
        Page<Task> taskPage = new PageImpl<>(tasks, pageRequest, 1);
        TaskResponse response = new TaskResponse(1L, "Title", "Desc", status, LocalDateTime.now(), null);
        Page<TaskResponse> expectedPage = new PageImpl<>(List.of(response), pageRequest, 1);

        when(taskRepository.findAllByIsCompleted(status, pageRequest)).thenReturn(taskPage);
        when(taskMapper.fromEntityToResponse(any(Task.class))).thenReturn(response);

        // Act
        Page<TaskResponse> result = taskService.findAllByIsCompleted(status, page, offset);

        // Assert
        assertEquals(expectedPage.getTotalElements(), result.getTotalElements());
        assertEquals(expectedPage.getContent(), result.getContent());
        verify(taskRepository).findAllByIsCompleted(status, pageRequest);
    }

    @Test
    @DisplayName("Обновление задачи: выбрасывает исключение, если задача не найдена")
    void update_shouldThrowNotFound_whenIdNotExists() {
        // Arrange
        Long id = 1L;
        TaskRequest request = new TaskRequest("Title", "Desc", CompletionStatus.NOT_COMPLETED, LocalDateTime.now(), null);
        doNothing().when(taskRequestValidator).validate(request);
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> taskService.update(request, id));
        assertEquals(ExceptionMessage.ENTITY_NOT_FOUND_BY_ID.getMessage(id), exception.getMessage());
        verify(taskRepository).findById(id);
        verifyNoMoreInteractions(taskMapper, taskRepository);
    }

    @Test
    @DisplayName("Удаление задачи: успешно удаляет задачу, если ID существует")
    void delete_shouldDeleteTask_whenIdExists() {
        // Arrange
        Long id = 1L;
        Task task = new Task(id, "Title", "Desc", CompletionStatus.NOT_COMPLETED, LocalDateTime.now(), null);
        TaskResponse response = new TaskResponse(id, "Title", "Desc", CompletionStatus.NOT_COMPLETED, LocalDateTime.now(), null);

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        doNothing().when(taskRepository).delete(task);
        when(taskMapper.fromEntityToResponse(task)).thenReturn(response);

        // Act
        TaskResponse result = taskService.delete(id);

        // Assert
        assertEquals(response, result);
        verify(taskRepository).findById(id);
        verify(taskRepository).delete(task);
        verify(taskMapper).fromEntityToResponse(task);
    }

    @Test
    @DisplayName("Удаление задачи: выбрасывает исключение, если задача не найдена")
    void delete_shouldThrowNotFound_whenIdNotExists() {
        // Arrange
        Long id = 1L;
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> taskService.delete(id));
        assertEquals(ExceptionMessage.ENTITY_NOT_FOUND_BY_ID.getMessage(id), exception.getMessage());
        verify(taskRepository).findById(id);
        verifyNoInteractions(taskMapper);
        verifyNoMoreInteractions(taskRepository);
    }
}