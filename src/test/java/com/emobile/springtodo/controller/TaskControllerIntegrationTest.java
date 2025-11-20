package com.emobile.springtodo.controller;

import com.emobile.springtodo.SpringToDoApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SpringToDoApplication.class)
@AutoConfigureMockMvc
@Testcontainers
public class TaskControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Интеграционный тест: Создание новой задачи - успешный сценарий")
    @Sql(scripts = {"classpath:/schema.sql", "classpath:drop-data.sql"})
    public void testCreateTask_Success() throws Exception {
        String requestJson = """
                {
                    "title": "New Task",
                    "description": "New Description",
                    "isCompleted": "NOT_COMPLETED"
                }
                """;

        String expectedJson = """
                {
                    "data": {
                        "title": "New Task",
                        "description": "New Description",
                        "isCompleted": "NOT_COMPLETED"
                    },
                    "errorCode": null,
                    "errorMessage": null,
                    "status": "OK"
                }
                """;
        String result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals(expectedJson, result, false);
    }

    @Test
    @DisplayName("Интеграционный тест: Поиск задачи по ID - успешный сценарий")
    @Sql(scripts = {"classpath:/schema.sql", "classpath:drop-data.sql", "classpath:insert-test-data.sql"})
    public void testFindById_Success() throws Exception {
        String expectedJson = """
                {
                    "data": {
                        "id": 1,
                        "title": "Predefined Task",
                        "description": "Description 1",
                        "isCompleted": "NOT_COMPLETED"
                    },
                    "errorCode": null,
                    "errorMessage": null,
                    "status": "OK"
                }
                """;

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tasks/1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals(expectedJson, result, false);
    }

    @Test
    @DisplayName("Интеграционный тест: Получение всех задач с пагинацией - успешный сценарий")
    @Sql(scripts = {"classpath:/schema.sql", "classpath:drop-data.sql", "classpath:insert-test-data.sql"})
    public void testFindAll_Success() throws Exception {
        String expectedJson = """
                {
                    "data": {
                        "content": [
                            {"id":1, "title":"Predefined Task", "description":"Description 1", "isCompleted":"NOT_COMPLETED"},
                            {"id":2, "title":"Predefined Task 2", "description":"Description 2", "isCompleted":"COMPLETED"}
                        ],
                        "totalElements": 2
                    },
                    "errorCode": null,
                    "errorMessage": null,
                    "status": "OK"
                }
                """;

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tasks/all")
                        .param("page", "0")
                        .param("offset", "10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals(expectedJson, result, false);
    }

    @Test
    @DisplayName("Интеграционный тест: Получение задач по статусу завершения - успешный сценарий")
    @Sql(scripts = {"classpath:/schema.sql", "classpath:drop-data.sql", "classpath:insert-test-data.sql"})
    public void testFindAllByIsCompleted_Success() throws Exception {
        String expectedJson = """
                {
                    "data": {
                        "content": [
                            {"id":2, "title":"Predefined Task 2", "description":"Description 2", "isCompleted":"COMPLETED"}
                        ],
                        "totalElements": 1
                    },
                    "errorCode": null,
                    "errorMessage": null,
                    "status": "OK"
                }
                """;

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tasks/by-is-completed")
                        .param("isCompleted", "COMPLETED")
                        .param("page", "0")
                        .param("offset", "10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals(expectedJson, result, false);
    }

    @Test
    @DisplayName("Интеграционный тест: Обновление задачи - успешный сценарий")
    @Sql(scripts = {"classpath:/schema.sql", "classpath:drop-data.sql", "classpath:insert-test-data.sql"})
    public void testUpdateTask_Success() throws Exception {
        String requestJson = """
                {
                    "title": "Updated Task",
                    "description": "Updated Description",
                    "isCompleted": "COMPLETED"
                }
                """;

        String expectedJson = """
                {
                    "data": {
                        "id": 1,
                        "title": "Updated Task",
                        "description": "Updated Description",
                        "isCompleted": "COMPLETED"
                    },
                    "errorCode": null,
                    "errorMessage": null,
                    "status": "OK"
                }
                """;

        String result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals(expectedJson, result, false);
    }

    @Test
    @DisplayName("Интеграционный тест: Удаление задачи - успешный сценарий")
    @Sql(scripts = {"classpath:/schema.sql", "classpath:drop-data.sql", "classpath:insert-test-data.sql"})
    public void testDeleteTask_Success() throws Exception {
        String expectedJson = """
                {
                    "data": {
                        "id": 1,
                        "title": "Predefined Task",
                        "description": "Description 1",
                        "isCompleted": "NOT_COMPLETED",
                        "createdAt":"2025-10-17T10:00:00",
                        "updatedAt":null
                    },
                    "errorCode": null,
                    "errorMessage": null,
                    "status": "OK"
                }
                """;

        String result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/tasks/1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JSONAssert.assertEquals(expectedJson, result, false);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tasks/1"))
                .andExpect(status().isOk());
    }


}