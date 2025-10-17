package com.emobile.springtodo.repository;

import com.emobile.springtodo.entity.Task;
import com.emobile.springtodo.entity.enumeration.CompletionStatus;
import com.emobile.springtodo.exception.EntityIsNullException;
import com.emobile.springtodo.exception.PageableIllegalArgumentException;
import com.emobile.springtodo.exception.TaskInvalidFieldException;
import com.emobile.springtodo.exception.TaskNotFoundException;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Testcontainers
class HibernateTaskRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private HibernateTaskRepository repository;

    @Autowired
    private SessionFactory sessionFactory;

    private Task createValidTask() {
        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Description");
        task.setIsCompleted(CompletionStatus.NOT_COMPLETED);
        task.setCreatedAt(LocalDateTime.now());
        return task;
    }

    @BeforeEach
    void setUp() {
        try (var session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createQuery("DELETE FROM Task").executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Test
    void save_shouldSaveNewTaskAndAssignId() {
        // Arrange
        Task task = createValidTask();

        // Act
        Task savedTask = repository.save(task);

        // Assert
        assertNotNull(savedTask.getId(), "Saved task should have an ID");
        assertEquals(task.getTitle(), savedTask.getTitle());
        assertEquals(task.getDescription(), savedTask.getDescription());
        assertEquals(task.getIsCompleted(), savedTask.getIsCompleted());
        assertNotNull(savedTask.getCreatedAt());
    }

    @Test
    void save_shouldThrowEntityIsNullException_forNullTask() {
        // Act & Assert
        EntityIsNullException exception = assertThrows(EntityIsNullException.class, () -> repository.save(null));
        assertEquals("Entity is null", exception.getMessage());
    }

    @Test
    void save_shouldThrowTaskInvalidFieldException_forNullTitle() {
        // Arrange
        Task task = createValidTask();
        task.setTitle(null);

        // Act & Assert
        TaskInvalidFieldException exception = assertThrows(TaskInvalidFieldException.class, () -> repository.save(task));
        assertEquals("Task title is null or empty: null", exception.getMessage());
    }

    @Test
    void findById_shouldReturnTask_whenExists() {
        // Arrange
        Task task = createValidTask();
        task = repository.save(task);

        // Act
        Optional<Task> found = repository.findById(task.getId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals(task.getId(), found.get().getId());
        assertEquals(task.getTitle(), found.get().getTitle());
    }

    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        // Act
        Optional<Task> found = repository.findById(999L);

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    void findById_shouldThrowTaskInvalidFieldException_forNullId() {
        // Act & Assert
        TaskInvalidFieldException exception = assertThrows(TaskInvalidFieldException.class, () -> repository.findById(null));
        assertEquals("Task id is null", exception.getMessage());
    }

    @Test
    void findAll_shouldReturnPagedTasks() {
        // Arrange
        Task task1 = createValidTask();
        Task task2 = createValidTask();
        task2.setTitle("Task 2");
        repository.save(task1);
        repository.save(task2);
        Pageable pageable = PageRequest.of(0, 1);

        // Act
        Page<Task> page = repository.findAll(pageable);

        // Assert
        assertEquals(1, page.getContent().size());
        assertEquals(2, page.getTotalElements());
        assertEquals(task1.getTitle(), page.getContent().getFirst().getTitle());
    }

    @Test
    void findAll_shouldThrowPageableIllegalArgumentException_forNullPageable() {
        // Act & Assert
        PageableIllegalArgumentException exception = assertThrows(PageableIllegalArgumentException.class, () -> repository.findAll(null));
        assertEquals("Pageable is null", exception.getMessage());
    }

    @Test
    void findAllByIsCompleted_shouldReturnPagedTasksByStatus() {
        // Arrange
        Task task1 = createValidTask();
        Task task2 = createValidTask();
        task2.setIsCompleted(CompletionStatus.COMPLETED);
        repository.save(task1);
        repository.save(task2);
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Task> page = repository.findAllByIsCompleted(CompletionStatus.NOT_COMPLETED, pageable);

        // Assert
        assertEquals(1, page.getContent().size());
        assertEquals(1, page.getTotalElements());
        assertEquals(CompletionStatus.NOT_COMPLETED, page.getContent().getFirst().getIsCompleted());
    }

    @Test
    void findAllByIsCompleted_shouldThrowTaskInvalidFieldException_forNullStatus() {
        // Act & Assert
        TaskInvalidFieldException exception = assertThrows(TaskInvalidFieldException.class, () -> repository.findAllByIsCompleted(null, PageRequest.of(0, 10)));
        assertEquals("Task isCompleted is null", exception.getMessage());
    }

    @Test
    void update_shouldUpdateExistingTask() {
        // Arrange
        Task task = createValidTask();
        task = repository.save(task);
        task.setTitle("Updated Title");
        task.setDescription("Updated Description");
        task.setIsCompleted(CompletionStatus.COMPLETED);

        // Act
        Task updated = repository.update(task);

        // Assert
        Optional<Task> found = repository.findById(task.getId());
        assertTrue(found.isPresent());
        assertEquals("Updated Title", found.get().getTitle());
        assertEquals("Updated Description", found.get().getDescription());
        assertEquals(CompletionStatus.COMPLETED, found.get().getIsCompleted());
        assertNotNull(found.get().getUpdatedAt());
    }

    @Test
    void update_shouldThrowTaskNotFoundException_forNonExistentTask() {
        // Arrange
        Task task = createValidTask();
        task.setId(999L);

        // Act & Assert
        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> repository.update(task));
        assertEquals("Entity not found by id: 999", exception.getMessage());
    }

    @Test
    void delete_shouldRemoveTask() {
        // Arrange
        Task task = createValidTask();
        task = repository.save(task);

        // Act
        repository.delete(task);

        // Assert
        Optional<Task> found = repository.findById(task.getId());
        assertFalse(found.isPresent());
    }

    @Test
    void delete_shouldThrowTaskNotFoundException_forNonExistentTask() {
        // Arrange
        Task task = createValidTask();
        task.setId(999L);

        // Act & Assert
        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> repository.delete(task));
        assertEquals("Entity not found by id: 999", exception.getMessage());
    }
}