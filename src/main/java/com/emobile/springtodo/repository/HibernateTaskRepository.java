package com.emobile.springtodo.repository;

import com.emobile.springtodo.entity.Task;
import com.emobile.springtodo.entity.enumeration.CompletionStatus;
import com.emobile.springtodo.entity.enumeration.ExceptionMessage;
import com.emobile.springtodo.exception.EntityIsNullException;
import com.emobile.springtodo.exception.PageableIllegalArgumentException;
import com.emobile.springtodo.exception.TaskInvalidFieldException;
import com.emobile.springtodo.exception.TaskNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.emobile.springtodo.validator.TaskRepositoryValidator.validateTask;

@Slf4j
@Repository
@RequiredArgsConstructor
public class HibernateTaskRepository implements TaskRepository {

    private final SessionFactory sessionFactory;

    @Override
    public Task save(Task task) {
        validateTask(task);

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            if (task.getId() == null) {
                session.persist(task);
            } else {
                session.merge(task);
            }
            session.getTransaction().commit();
            return task;
        } catch (Exception e) {
            log.error("Error while saving task {}", task, e);
            throw e;
        }
    }

    @Override
    public Optional<Task> findById(Long id) {
        if (id == null) {
            throw new TaskInvalidFieldException(
                    ExceptionMessage.ID_IS_NULL.getMessage(),
                    ExceptionMessage.ID_IS_NULL.getErrorCode()
            );
        }

        try (Session session = sessionFactory.openSession()) {
            Task task = session.get(Task.class, id);
            return Optional.ofNullable(task);
        }
    }

    @Override
    public Page<Task> findAll(Pageable pageable) {
        if (pageable == null) {
            throw new PageableIllegalArgumentException(
                    ExceptionMessage.PAGEABLE_IS_NULL.getMessage(),
                    ExceptionMessage.PAGEABLE_IS_NULL.getErrorCode()
            );
        }

        try (Session session = sessionFactory.openSession()) {
            Query<Task> query = session.createQuery("FROM Task ORDER BY id", Task.class);
            applyPagination(query, pageable);
            List<Task> tasks = query.list();

            Long total = session.createQuery("SELECT COUNT(t) FROM Task t", Long.class).uniqueResult();

            return new PageImpl<>(tasks, pageable, total != null ? total : 0);
        }
    }

    @Override
    public Page<Task> findAllByIsCompleted(CompletionStatus isCompleted, Pageable pageable) {
        if (isCompleted == null) {
            throw new TaskInvalidFieldException(
                    ExceptionMessage.COMPLETED_IS_NULL.getMessage(),
                    ExceptionMessage.COMPLETED_IS_NULL.getErrorCode()
            );
        }

        if (pageable == null) {
            throw new PageableIllegalArgumentException(
                    ExceptionMessage.PAGEABLE_IS_NULL.getMessage(),
                    ExceptionMessage.PAGEABLE_IS_NULL.getErrorCode()
            );
        }

        try (Session session = sessionFactory.openSession()) {
            Query<Task> query = session.createQuery("FROM Task t WHERE t.isCompleted = :status ORDER BY id", Task.class);
            query.setParameter("status", isCompleted);
            applyPagination(query, pageable);
            List<Task> tasks = query.list();

            Long total = session.createQuery("SELECT COUNT(t) FROM Task t WHERE t.isCompleted = :status", Long.class)
                    .setParameter("status", isCompleted)
                    .uniqueResult();

            return new PageImpl<>(tasks, pageable, total != null ? total : 0);
        }
    }

    @Override
    public Task update(final Task task) {
        if (task == null) {
            throw new EntityIsNullException(
                    ExceptionMessage.ENTITY_IS_NULL.getMessage(),
                    ExceptionMessage.ENTITY_IS_NULL.getErrorCode());
        }
        if (task.getId() == null) {
            throw new TaskInvalidFieldException(
                    ExceptionMessage.ID_IS_NULL.getMessage(),
                    ExceptionMessage.ID_IS_NULL.getErrorCode());
        }
        validateTask(task);

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Task existing = session.get(Task.class, task.getId());
            if (existing == null) {
                throw new TaskNotFoundException(
                        ExceptionMessage.ENTITY_NOT_FOUND_BY_ID.getMessage(task.getId()),
                        ExceptionMessage.ENTITY_NOT_FOUND_BY_ID.getErrorCode());
            }
            existing.setTitle(task.getTitle());
            existing.setDescription(task.getDescription());
            existing.setIsCompleted(task.getIsCompleted());
            existing.setUpdatedAt(LocalDateTime.now());
            session.merge(existing);
            session.getTransaction().commit();
            return existing;
        }
    }

    @Override
    public void delete(final Task task) {
        if (task == null) {
            throw new EntityIsNullException(
                    ExceptionMessage.ENTITY_IS_NULL.getMessage(),
                    ExceptionMessage.ENTITY_IS_NULL.getErrorCode());
        }
        if (task.getId() == null) {
            throw new TaskInvalidFieldException(
                    ExceptionMessage.ID_IS_NULL.getMessage(),
                    ExceptionMessage.ID_IS_NULL.getErrorCode());
        }

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Task toDelete = session.get(Task.class, task.getId());
            if (toDelete == null) {
                throw new TaskNotFoundException(
                        ExceptionMessage.ENTITY_NOT_FOUND_BY_ID.getMessage(task.getId()),
                        ExceptionMessage.ENTITY_NOT_FOUND_BY_ID.getErrorCode());
            }
            session.remove(toDelete);
            session.getTransaction().commit();
        }
    }


    public void applyPagination(Query<?> query, Pageable pageable) {
        query.setFirstResult((int)  pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
    }
}
