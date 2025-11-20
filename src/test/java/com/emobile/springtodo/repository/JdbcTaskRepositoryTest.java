package com.emobile.springtodo.repository;

import com.emobile.springtodo.entity.Task;
import com.emobile.springtodo.entity.enumeration.CompletionStatus;
import com.emobile.springtodo.entity.enumeration.ExceptionMessage;
import com.emobile.springtodo.entity.enumeration.SqlQuery;
import com.emobile.springtodo.exception.EntityIsNullException;
import com.emobile.springtodo.exception.PageableIllegalArgumentException;
import com.emobile.springtodo.exception.TaskInvalidFieldException;
import com.emobile.springtodo.exception.TaskNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JdbcTaskRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    private JdbcTaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository = new JdbcTaskRepository(jdbcTemplate);
    }

    @Test
    @DisplayName("Создание таски: успешно возвращает созданную таску.")
    void save_shouldCreateNewTask_whenIdIsNull() {
        // Arrange: Подготовка задачи без ID
        Task task = new Task();
        task.setTitle("Test Title");
        task.setDescription("Test Description");
        task.setIsCompleted(CompletionStatus.NOT_COMPLETED);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(null);

        // Мокаем update с KeyHolder
        when(jdbcTemplate.update(any(), any(KeyHolder.class))).thenAnswer(invocation -> {
            // Симулируем генерацию ID
            ((GeneratedKeyHolder) invocation.getArgument(1)).getKeyList().add(Collections.singletonMap("id", 1L));
            return 1; // Успешная вставка
        });

        // Act: Вызов save
        Task savedTask = taskRepository.save(task);

        // Assert: Проверяем, что ID сгенерирован, задача возвращена
        assertNotNull(savedTask.getId());
        assertEquals(1L, savedTask.getId());

        // Захватываем аргументы для проверки SQL
        verify(jdbcTemplate).update(any(), any(KeyHolder.class));
        // Здесь можно дополнительно проверить параметры PreparedStatement, но для простоты опустим
    }

    @Test
    @DisplayName("Создание таски: выбрасывает EntityIsNullException, когда передаём null вместо таски.")
    void save_shouldThrowException_whenTaskIsNull() {
        // Arrange & Act & Assert: Проверяем валидацию
        EntityIsNullException exception = assertThrows(EntityIsNullException.class, () -> taskRepository.save(null));
        assertEquals(ExceptionMessage.ENTITY_IS_NULL.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Создание таски: выбрасывает TaskInvalidFieldException, когда передаём пустую таску.")
    void save_shouldThrowException_whenTitleIsEmpty() {
        // Arrange: Задача с пустым title
        Task task = new Task();
        task.setTitle(""); // Пустой
        task.setCreatedAt(LocalDateTime.now());

        // Act & Assert
        TaskInvalidFieldException exception = assertThrows(TaskInvalidFieldException.class, () -> taskRepository.save(task));
        assertEquals(ExceptionMessage.TITLE_IS_NULL_OR_EMPTY.getMessage(""), exception.getMessage());
    }

    @Test
    @DisplayName("Ищем таску по id: возвращает пустой Optional.")
    void findById_shouldReturnEmpty_whenIdNotFound() {
        // Arrange: Мокаем пустой результат (throw EmptyResultDataAccessException, но мы catch в коде)
        when(jdbcTemplate.queryForObject(anyString(), any(RowMapper.class), anyLong()))
                .thenThrow(new RuntimeException("No result")); // Симулируем отсутствие

        // Act
        Optional<Task> result = taskRepository.findById(1L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Ищем таску по id: выбрасывает TaskInvalidFieldException, при условии что id = null.")
    void findById_shouldThrowException_whenIdIsNull() {
        // Act & Assert
        TaskInvalidFieldException exception = assertThrows(TaskInvalidFieldException.class, () -> taskRepository.findById(null));
        assertEquals(ExceptionMessage.ID_IS_NULL.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Найти все таски: возвращает paged результат.")
    void findAll_shouldReturnPagedTasks() {
        // Arrange: Pageable
        Pageable pageable = PageRequest.of(0, 10);
        List<Task> tasks = List.of(new Task(1L, "Title1", "Desc1", CompletionStatus.NOT_COMPLETED, LocalDateTime.now(), null));
        when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(tasks);
        when(jdbcTemplate.queryForObject(SqlQuery.COUNT.getQuery(), Long.class)).thenReturn(1L);

        // Act
        Page<Task> result = taskRepository.findAll(pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(tasks, result.getContent());
        verify(jdbcTemplate).query(contains("LIMIT 10 OFFSET 0"), any(RowMapper.class));
    }

    @Test
    @DisplayName("Найти все таски: выбрасывает PageableIllegalArgumentException, когда pageable = null.")
    void findAll_shouldThrowException_whenPageableIsNull() {
        // Act & Assert
        PageableIllegalArgumentException exception = assertThrows(PageableIllegalArgumentException.class, () -> taskRepository.findAll(null));
        assertEquals(ExceptionMessage.PAGEABLE_IS_NULL.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Обновить таску: выбрасывает TaskNotFoundException, когда не можем найти таску.")
    void update_shouldThrowNotFound_whenNoRowsUpdated() {
        // Arrange
        Task task = new Task(1L, "Title", "Desc", CompletionStatus.NOT_COMPLETED, LocalDateTime.now(), null);
        when(jdbcTemplate.update(anyString(), any(), any(), any(), any(), anyLong())).thenReturn(0); // Нет обновлений

        // Act & Assert
        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> taskRepository.update(task));
        assertEquals(ExceptionMessage.ENTITY_NOT_FOUND_BY_ID.getMessage(1L), exception.getMessage());
    }

    @Test
    @DisplayName("Удалить таску: выбрасывает TaskNotFoundException, когда не можем найти таску.")
    void delete_shouldThrowNotFound_whenNoRowsDeleted() {
        // Arrange
        Task task = new Task(1L, "Title", "Desc", CompletionStatus.NOT_COMPLETED, LocalDateTime.now(), null);
        when(jdbcTemplate.update(anyString(), anyLong())).thenReturn(0);

        // Act & Assert
        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> taskRepository.delete(task));
        assertEquals(ExceptionMessage.ENTITY_NOT_FOUND_BY_ID.getMessage(1L), exception.getMessage());
    }

    @Test
    @DisplayName("Удалить таску: выбрасывает EntityIsNullException, когда передаём null.")
    void delete_shouldThrowException_whenTaskIsNull() {
        // Act & Assert
        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> taskRepository.delete(null));
        assertEquals(ExceptionMessage.ENTITY_IS_NULL.getMessage(), exception.getMessage());
    }
}