package com.emobile.springtodo.repository;

import com.emobile.springtodo.entity.Task;
import com.emobile.springtodo.entity.enumeration.CompletionStatus;
import com.emobile.springtodo.entity.enumeration.ExceptionMessage;
import com.emobile.springtodo.entity.enumeration.SqlQuery;
import com.emobile.springtodo.exception.EntityIsNullException;
import com.emobile.springtodo.exception.PageableIllegalArgumentException;
import com.emobile.springtodo.exception.TaskInvalidFieldException;
import com.emobile.springtodo.exception.TaskNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class TaskRepositoryImpl implements TaskRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Task> rowMapper = (rs, rowNum) -> {
        Task task = new Task();
        task.setId(rs.getLong("id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));
        task.setIsCompleted(CompletionStatus.valueOf(rs.getString("is_completed")));
        task.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            task.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        return task;
    };

    @Override
    public Task save(final Task task) {
        validateTask(task);

        if (task.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        SqlQuery.SAVE.getQuery(),
                        new String[]{"id"}
                );
                ps.setString(1, task.getTitle());
                ps.setString(2, task.getDescription());
                ps.setString(3, task.getIsCompleted().name());
                ps.setTimestamp(4, Timestamp.valueOf(task.getCreatedAt()));
                ps.setTimestamp(5, task.getUpdatedAt() != null ? Timestamp.valueOf(task.getUpdatedAt()) : null);
                return ps;
            }, keyHolder);

            Long generatedId = Objects.requireNonNull(keyHolder.getKey()).longValue();
            task.setId(generatedId);
            return task;
        }
        return update(task);
    }

    @Override
    public Optional<Task> findById(final Long id) {
        if (id == null) {
            throw new TaskInvalidFieldException(
                    ExceptionMessage.ID_IS_NULL.getMessage(),
                    ExceptionMessage.ID_IS_NULL.getErrorCode());
        }

        try {
            Task task = jdbcTemplate.queryForObject(
                    SqlQuery.FIND_BY_ID.getQuery(),
                    rowMapper,
                    id
            );
            return Optional.ofNullable(task);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Page<Task> findAll(final Pageable pageable) {
        if (pageable == null) {
            throw new PageableIllegalArgumentException(
                    ExceptionMessage.PAGEABLE_IS_NULL.getMessage(),
                    ExceptionMessage.PAGEABLE_IS_NULL.getErrorCode());
        }

        String query = SqlQuery.FIND_ALL.getQuery() + buildPageableClause(pageable);
        List<Task> tasks = jdbcTemplate.query(query, rowMapper);
        Long total = jdbcTemplate.queryForObject(SqlQuery.COUNT.getQuery(), Long.class);

        return new PageImpl<>(tasks, pageable, total != null ? total : 0);
    }

    @Override
    public Page<Task> findAllByIsCompleted(final CompletionStatus isCompleted, final Pageable pageable) {
        if (isCompleted == null) {
            throw new TaskInvalidFieldException(
                    ExceptionMessage.COMPLETED_IS_NULL.getMessage(),
                    ExceptionMessage.COMPLETED_IS_NULL.getErrorCode());
        }
        if (pageable == null) {
            throw new PageableIllegalArgumentException(
                    ExceptionMessage.PAGEABLE_IS_NULL.getMessage(),
                    ExceptionMessage.PAGEABLE_IS_NULL.getErrorCode());
        }
        String query = SqlQuery.FIND_ALL_BY_COMPLETED.getQuery() + buildPageableClause(pageable);
        List<Task> tasks = jdbcTemplate.query(query, rowMapper, isCompleted.name());
        Long total = jdbcTemplate.queryForObject(
                SqlQuery.COUNT_BY_COMPLETED.getQuery(),
                Long.class,
                isCompleted.name()
        );
        return new PageImpl<>(tasks, pageable, total != null ? total : 0);
    }

    @Override
    public Task update(final Task task) {
        validateTask(task);

        if (task.getId() == null) {
            throw new TaskInvalidFieldException(
                    ExceptionMessage.ID_IS_NULL.getMessage(),
                    ExceptionMessage.ID_IS_NULL.getErrorCode());
        }

        int updatedRows = jdbcTemplate.update(
                SqlQuery.UPDATE.getQuery(),
                task.getTitle(),
                task.getDescription(),
                task.getIsCompleted().name(),
                LocalDateTime.now(),
                task.getId()
        );

        if (updatedRows == 0) {
            throw new TaskNotFoundException(
                    ExceptionMessage.ENTITY_NOT_FOUND_BY_ID.getMessage(task.getId()),
                    ExceptionMessage.ENTITY_NOT_FOUND_BY_ID.getErrorCode());
        }

        return task;
    }

    @Override
    public void delete(final Task task) {
        if (task == null) {
            throw new TaskNotFoundException(
                    ExceptionMessage.ENTITY_IS_NULL.getMessage(),
                    ExceptionMessage.ENTITY_IS_NULL.getErrorCode());
        }

        int deletedRows = jdbcTemplate.update(SqlQuery.DELETE.getQuery(), task.getId());
        if (deletedRows == 0) {
            throw new TaskNotFoundException(
                    ExceptionMessage.ENTITY_NOT_FOUND_BY_ID.getMessage(task.getId()),
                    ExceptionMessage.ENTITY_NOT_FOUND_BY_ID.getErrorCode());
        }
    }

    private void validateTask(final Task task) {
        if (task == null) {
            throw new EntityIsNullException(
                    ExceptionMessage.ENTITY_IS_NULL.getMessage(),
                    ExceptionMessage.ENTITY_IS_NULL.getErrorCode());
        }
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            throw new TaskInvalidFieldException(
                    ExceptionMessage.TITLE_IS_NULL_OR_EMPTY.getMessage(task.getTitle()),
                    ExceptionMessage.TITLE_IS_NULL_OR_EMPTY.getErrorCode());
        }
        if (task.getCreatedAt() == null) {
            throw new TaskInvalidFieldException(
                    ExceptionMessage.CREATED_AT_IS_NULL.getMessage(),
                    ExceptionMessage.CREATED_AT_IS_NULL.getErrorCode());
        }
    }

    private String buildPageableClause(final Pageable pageable) {
        return String.format(
                " ORDER BY id LIMIT %d OFFSET %d",
                pageable.getPageSize(),
                pageable.getPageNumber() * pageable.getPageSize()
        );
    }
}