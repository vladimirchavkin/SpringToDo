package com.emobile.springtodo.repository;

import com.emobile.springtodo.entity.Task;
import com.emobile.springtodo.entity.enumeration.CompletionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaTaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE t.isCompleted = :status")
    Page<Task> findAllByIsCompleted(@Param("status") CompletionStatus isCompleted, Pageable pageable);
}
