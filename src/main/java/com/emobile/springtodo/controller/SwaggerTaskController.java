package com.emobile.springtodo.controller;

import com.emobile.springtodo.controller.response.CustomResponse;
import com.emobile.springtodo.entity.dto.TaskRequest;
import com.emobile.springtodo.entity.dto.TaskResponse;
import com.emobile.springtodo.entity.enumeration.CompletionStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.domain.Page;

@Tag(name = "Task Controller", description = "Контроллер для работы с задачами.")
public interface SwaggerTaskController {

    @Operation(
            summary = "Создать задачу",
            description = "Создать задачу по заданному DTO",
            responses = {
                    @ApiResponse(
                            description = "Задача создана",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TaskResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                            "id": 1,
                                                            "title": "Сходить в магазин",
                                                            "description": "Купить масло, хлеб, яйца. И мороженое",
                                                            "isCompleted": "NOT_COMPLETED"
                                                            "createdAt": "2023-01-01T00:00:00",
                                                            "updatedAt": "2023-02-01T00:00:00"
                                                            
                                                            """
                                            )
                                    }
                            )
                    )
            }
    )
    CustomResponse<TaskResponse> create(@Valid TaskRequest taskRequest);

    @Operation(
            summary = "Найти задачу по id",
            description = "Найти задачу по заданному id",
            responses = {
                    @ApiResponse(
                            description = "Задача найдена",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TaskResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                            "id": 1,
                                                            "title": "Сходить в магазин",
                                                            "description": "Купить масло, хлеб, яйца. И мороженое",
                                                            "isCompleted": "NOT_COMPLETED"
                                                            "createdAt": "2023-01-01T00:00:00",
                                                            "updatedAt": "2023-02-01T00:00:00"
                                                            """
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            description = "Задача не найдена",
                            responseCode = "404",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TaskResponse.class)
                            )
                    )
            }
    )
    CustomResponse<TaskResponse> findById(@Positive Long id);

    @Operation(
            summary = "Найти все задачи",
            description = "Найти все задачи по заданным параметрам",
            responses = {
                    @ApiResponse(
                            description = "Задачи найдены",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TaskResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                            tasks: [
                                                                {
                                                                    "id": 1,
                                                                    "title": "Сходить в магазин",
                                                                    "description": "Купить масло, хлеб, яйца. И мороженое",
                                                                    "isCompleted": "NOT_COMPLETED"
                                                                    "createdAt": "2023-02-01T12:00:00",
                                                                    "updatedAt": "2023-02-01T12:00:00"
                                                                },
                                                                {
                                                                    "id": 2,
                                                                    "title": "Убраться дома",
                                                                    "description": "Пропылесосить и помыть пол.",
                                                                    "isCompleted": "NOT_COMPLETED"
                                                                    "createdAt": "2023-01-01T15:00:00",
                                                                    "updatedAt": "2023-02-01T15:00:00"
                                                                }
                                                            ]
                                                            """
                                            )
                                    }
                            )
                    )
            }
    )
    CustomResponse<Page<TaskResponse>> findAll(@PositiveOrZero Integer page, @Positive Integer offset);

    @Operation(
            summary = "Найти все задачи по isCompleted",
            description = "Найти все задачи по заданным параметрам",
            responses = {
                    @ApiResponse(
                            description = "Задачи найдены",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TaskResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                                "id": 1,
                                                                "title": "Сходить в магазин",
                                                                "description": "Купить масло, хлеб, яйца. И мороженое",
                                                                "isCompleted": "COMPLETED"
                                                                "createdAt": "2023-02-01T12:00:00",
                                                                "updatedAt": "2023-02-01T12:00:00"
                                                            """
                                            )
                                    }
                            )
                    )
            }
    )
    CustomResponse<Page<TaskResponse>> findAllByIsCompleted(
            CompletionStatus isCompleted,
            @PositiveOrZero Integer page,
            @Positive Integer offset
    );

    @Operation(
            summary = "Обновить задачу",
            description = "Обновить задачу по заданному id",
            responses = {
                    @ApiResponse(
                            description = "Задача обновлена",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TaskResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                            "id": 1,
                                                            "title": "Сходить в магазин",
                                                            "description": "Купить масло, хлеб, яйца. И мороженое - 2шт",
                                                            "isCompleted": "NOT_COMPLETED"
                                                            "createdAt": "2023-02-01T12:00:00",
                                                            "updatedAt": "2023-02-01T13:00:00"
                                                            """
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            description = "Задача не найдена",
                            responseCode = "404",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TaskResponse.class)
                            )
                    )
            }
    )
    CustomResponse<TaskResponse> update(@Valid TaskRequest taskRequest, @Positive Long id);

    @Operation(
            summary = "Удалить задачу",
            description = "Удалить задачу по заданному id",
            responses = {
                    @ApiResponse(
                            description = "Задача удалена",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TaskResponse.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = """
                                                            "id": 1,
                                                            "title": "Сходить в магазин",
                                                            "description": "Купить масло, хлеб, яйца. И мороженое - 2шт",
                                                            "isCompleted": "NOT_COMPLETED"
                                                            "createdAt": "2023-02-01T12:00:00",
                                                            "updatedAt": "2023-02-01T13:00:00"
                                                            """
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            description = "Задача не найдена",
                            responseCode = "404",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TaskResponse.class)
                            )
                    )
            }
    )
    CustomResponse<TaskResponse> delete(@Positive Long id);

}
