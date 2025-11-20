package com.emobile.springtodo.repository;

import com.emobile.springtodo.entity.Task;
import com.emobile.springtodo.entity.enumeration.CompletionStatus;
import com.emobile.springtodo.exception.EntityIsNullException;
import com.emobile.springtodo.exception.PageableIllegalArgumentException;
import com.emobile.springtodo.exception.TaskInvalidFieldException;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностью {@link Task}.
 * Предоставляет методы для сохранения, поиска, обновления и удаления задач.
 */
public interface TaskRepository {

    /**
     * Сохраняет задачу в базе данных.
     *
     * @param task сущность задачи для сохранения.
     * @return сохранённая задача.
     * @throws EntityIsNullException если переданная сущность null или некорректна.
     * @throws DataAccessException   если произошла ошибка при сохранении.
     */
    Task save(Task task);

    /**
     * Находит задачу по её идентификатору.
     *
     * @param id идентификатор задачи.
     * @return {@link Optional}, содержащий задачу, если она найдена, или пустой, если не найдена.
     * @throws TaskInvalidFieldException если идентификатор null.
     * @throws DataAccessException       если произошла ошибка при поиске.
     */
    Optional<Task> findById(Long id);

    /**
     * Возвращает все задачи с поддержкой пагинации и сортировки.
     *
     * @param pageable объект, содержащий параметры пагинации и сортировки.
     * @return страница с задачами.
     * @throws PageableIllegalArgumentException если параметры пагинации некорректны.
     * @throws DataAccessException              если произошла ошибка при поиске.
     */
    Page<Task> findAll(Pageable pageable);

    /**
     * Находит задачи по статусу выполнения с поддержкой пагинации и сортировки.
     *
     * @param isCompleted статус выполнения задачи.
     * @param pageable объект, содержащий параметры пагинации и сортировки.
     * @return страница с задачами, соответствующими указанному статусу.
     * @throws PageableIllegalArgumentException если параметры пагинации или статус некорректны.
     * @throws DataAccessException              если произошла ошибка при поиске.
     */
    Page<Task> findAllByIsCompleted(CompletionStatus isCompleted, Pageable pageable);

    /**
     * Обновляет существующую задачу в базе данных.
     *
     * @param task сущность задачи с обновлёнными данными
     * @return обновлённая задача
     * @throws EntityIsNullException если переданная сущность null или некорректна
     * @throws DataAccessException   если произошла ошибка при обновлении
     */
    Task update(Task task);

    /**
     * Удаляет задачу из базы данных.
     *
     * @param task сущность задачи для удаления
     * @throws EntityIsNullException если переданная сущность null
     * @throws DataAccessException   если произошла ошибка при удалении
     */
    void delete(Task task);
}