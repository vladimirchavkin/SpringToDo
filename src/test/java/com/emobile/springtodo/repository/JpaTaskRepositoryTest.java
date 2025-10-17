package com.emobile.springtodo.repository;

import com.emobile.springtodo.entity.Task;
import com.emobile.springtodo.entity.enumeration.CompletionStatus;
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
class JpaTaskRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private JpaTaskRepository repository;

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
        repository.deleteAll();
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
    void save_shouldThrowException_forNullTask() {
        // Act & Assert
        assertThrows(Exception.class, () -> repository.save(null));
    }

    @Test
    void save_shouldThrowException_forNullTitle() {
        // Arrange
        Task task = createValidTask();
        task.setTitle(null);

        // Act & Assert
        assertThrows(Exception.class, () -> repository.save(task));
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
        assertEquals(task1.getTitle(), page.getContent().get(0).getTitle());
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
        assertEquals(CompletionStatus.NOT_COMPLETED, page.getContent().get(0).getIsCompleted());
    }

    @Test
    void update_shouldUpdateExistingTask() {
        // Arrange
        Task task = createValidTask();
        task = repository.save(task);
        task.setTitle("Updated Title");
        task.setDescription("Updated Description");
        task.setIsCompleted(CompletionStatus.COMPLETED);
        task.setUpdatedAt(LocalDateTime.now());

        // Act
        Task updated = repository.save(task);

        // Assert
        Optional<Task> found = repository.findById(task.getId());
        assertTrue(found.isPresent());
        assertEquals("Updated Title", found.get().getTitle());
        assertEquals("Updated Description", found.get().getDescription());
        assertEquals(CompletionStatus.COMPLETED, found.get().getIsCompleted());
        assertNotNull(found.get().getUpdatedAt());
    }

    @Test
    void delete_shouldRemoveTask() {
        // Arrange
        Task task = createValidTask();
        task = repository.save(task);

        // Act
        repository.deleteById(task.getId());

        // Assert
        Optional<Task> found = repository.findById(task.getId());
        assertFalse(found.isPresent());
    }
}