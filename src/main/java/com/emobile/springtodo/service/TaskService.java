package com.emobile.springtodo.service;

import com.emobile.springtodo.entity.dto.TaskRequest;
import com.emobile.springtodo.entity.dto.TaskResponse;
import com.emobile.springtodo.entity.enumeration.CompletionStatus;
import com.emobile.springtodo.exception.*;
import org.springframework.data.domain.Page;

/**
 * Сервис для управления задачами.
 * Предоставляет методы для создания, поиска, обновления и удаления задач.
 */
public interface TaskService {

    /**
     * Создаёт новую задачу на основе переданного запроса.
     *
     * @param taskRequest объект запроса, содержащий данные для создания задачи.
     * @return созданная задача.
     * @throws UniqueViolationException если сущность совпадает с уже существующей по id, title.
     * @throws EntityIsNullException    если переданная сущность null или некорректна.
     */
    TaskResponse create(TaskRequest taskRequest);

    /**
     * Находит задачу по её идентификатору.
     *
     * @param id идентификатор задачи.
     * @return объект ответа с данными задачи.
     * @throws TaskNotFoundException если задача с указанным идентификатором не найдена.
     */
    TaskResponse findById(Long id);

    /**
     * Возвращает список всех задач с поддержкой пагинации.
     *
     * @param page   номер страницы (начинается с 0).
     * @param offset количество задач на странице.
     * @return страница с объектами ответа, содержащими данные задач.
     * @throws PageableIllegalArgumentException если параметры пагинации некорректны.
     */
    Page<TaskResponse> findAll(Integer page, Integer offset);

    /**
     * Находит задачи по статусу выполнения с поддержкой пагинации.
     *
     * @param isCompleted статус выполнения задачи.
     * @param page        номер страницы (начинается с 0).
     * @param offset      количество задач на странице.
     * @return страница с объектами ответа, содержащими данные задач.
     * @throws TaskInvalidFieldException        если статус выполнения задачи некорректен.
     * @throws PageableIllegalArgumentException если параметры пагинации некорректны.
     */
    Page<TaskResponse> findAllByIsCompleted(CompletionStatus isCompleted, Integer page, Integer offset);

    /**
     * Обновляет существующую задачу на основе переданного запроса.
     *
     * @param taskRequest объект запроса с обновлёнными данными задачи.
     * @param id          идентификатор задачи.
     * @return обновлённая задача.
     * @throws TaskNotFoundException     если задача с указанным идентификатором не найдена.
     * @throws TaskInvalidFieldException если данные в запросе некорректны.
     */
    TaskResponse update(TaskRequest taskRequest, Long id);

    /**
     * Удаляет задачу по её идентификатору.
     *
     * @param id идентификатор задачи.
     * @return удалённая задача.
     * @throws TaskNotFoundException если задача с указанным идентификатором не найдена.
     * @throws TaskInvalidFieldException если данные в запросе некорректны.
     */
    TaskResponse delete(Long id);
}