package com.emobile.springtodo.mapper;

import com.emobile.springtodo.entity.Task;
import com.emobile.springtodo.entity.dto.TaskRequest;
import com.emobile.springtodo.entity.dto.TaskResponse;
import org.mapstruct.*;

/**
 * Интерфейс для маппинга между сущностью {@link Task} и DTO ({@link TaskRequest}, {@link TaskResponse}).
 * Используется для преобразования данных между слоями приложения.
 */
@Mapper(componentModel = "spring")
public interface TaskMapper {

    /**
     * Преобразует сущность {@link Task} в DTO {@link TaskResponse} для ответа клиенту.
     *
     * @param task сущность задачи
     * @return DTO с данными задачи
     */
    TaskResponse fromEntityToResponse(Task task);

    /**
     * Преобразует DTO {@link TaskRequest} в сущность {@link Task} для сохранения в базе данных.
     * Поля {@code id} и {@code createdAt} игнорируются, так как устанавливаются на уровне сервиса или базы данных.
     *
     * @param taskRequest DTO с данными запроса
     * @return сущность задачи
     */
    @Mapping(target = "createdAt", ignore = true)
    Task fromRequestToEntity(TaskRequest taskRequest);

    /**
     * Обновляет существующую сущность {@link Task} на основе данных из {@link TaskRequest}.
     * Поля {@code id} и {@code createdAt} игнорируются, чтобы сохранить идентификатор и дату уже существующей задачи.
     *
     * @param task        сущность задачи, подлежащая обновлению
     * @param taskRequest DTO с новыми данными
     * @return обновлённая сущность задачи
     */

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    Task updateEntityFromRequest(@MappingTarget Task task, TaskRequest taskRequest);
}