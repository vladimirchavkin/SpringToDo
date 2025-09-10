package com.emobile.springtodo.controller;

import com.emobile.springtodo.entity.dto.TaskRequest;
import com.emobile.springtodo.entity.dto.TaskResponse;
import com.emobile.springtodo.entity.enumeration.CompletionStatus;
import com.emobile.springtodo.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)

class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private TaskRequest taskRequest;
    private TaskResponse taskResponse;
    private Page<TaskResponse> taskResponsePage;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        taskRequest = new TaskRequest(
                "Test Task",
                "Test Description",
                CompletionStatus.NOT_COMPLETED,
                LocalDateTime.now(),
                null
        );

        taskResponse = new TaskResponse(
                1L,
                "Test Task",
                "Test Description",
                CompletionStatus.NOT_COMPLETED,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        taskResponsePage = new PageImpl<>(List.of(taskResponse), PageRequest.of(0, 10), 1);
    }

    @Test
    void createTask_Success_ReturnsCreatedTask() throws Exception {
        when(taskService.create(any(TaskRequest.class))).thenReturn(taskResponse);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(taskResponse.id()))
                .andExpect(jsonPath("$.data.title").value(taskResponse.title()))
                .andExpect(jsonPath("$.data.description").value(taskResponse.description()))
                .andExpect(jsonPath("$.data.isCompleted").value(taskResponse.isCompleted().toString()));

        verify(taskService).create(any(TaskRequest.class));
    }

    @Test
    void findById_Success_ReturnsTask() throws Exception {
        when(taskService.findById(1L)).thenReturn(taskResponse);

        mockMvc.perform(get("/api/v1/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(taskResponse.id()))
                .andExpect(jsonPath("$.data.title").value(taskResponse.title()))
                .andExpect(jsonPath("$.data.description").value(taskResponse.description()))
                .andExpect(jsonPath("$.data.isCompleted").value(taskResponse.isCompleted().toString()));

        verify(taskService).findById(1L);
    }

    @Test
    void findAll_Success_ReturnsPagedTasks() throws Exception {
        when(taskService.findAll(0, 10)).thenReturn(taskResponsePage);

        mockMvc.perform(get("/api/v1/tasks/all")
                        .param("page", "0")
                        .param("offset", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(taskResponse.id()))
                .andExpect(jsonPath("$.data.content[0].title").value(taskResponse.title()))
                .andExpect(jsonPath("$.data.totalElements").value(1));

        verify(taskService).findAll(0, 10);
    }

    @Test
    void findAllByIsCompleted_Success_ReturnsPagedTasks() throws Exception {
        when(taskService.findAllByIsCompleted(CompletionStatus.NOT_COMPLETED, 0, 10))
                .thenReturn(taskResponsePage);

        mockMvc.perform(get("/api/v1/tasks/by-is-completed")
                        .param("isCompleted", CompletionStatus.NOT_COMPLETED.toString())
                        .param("page", "0")
                        .param("offset", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(taskResponse.id()))
                .andExpect(jsonPath("$.data.content[0].title").value(taskResponse.title()))
                .andExpect(jsonPath("$.data.totalElements").value(1));

        verify(taskService).findAllByIsCompleted(CompletionStatus.NOT_COMPLETED, 0, 10);
    }

    @Test
    void deleteTask_Success_ReturnsDeletedTask() throws Exception {
        when(taskService.delete(1L)).thenReturn(taskResponse);

        mockMvc.perform(delete("/api/v1/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(taskResponse.id()))
                .andExpect(jsonPath("$.data.title").value(taskResponse.title()))
                .andExpect(jsonPath("$.data.description").value(taskResponse.description()))
                .andExpect(jsonPath("$.data.isCompleted").value(taskResponse.isCompleted().toString()));

        verify(taskService).delete(1L);
    }
}