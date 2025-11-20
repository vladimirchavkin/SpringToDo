package com.emobile.springtodo.entity.enumeration;

import lombok.Getter;

@Getter
public enum SqlQuery {
    SAVE("INSERT INTO tasks (title, description, is_completed, created_at, updated_at) VALUES (?, ?, ?, ?, ?)"),
    UPDATE("UPDATE tasks SET title = ?, description = ?, is_completed = ?, updated_at = ? WHERE id = ?"),
    FIND_BY_ID("SELECT id, title, description, is_completed, created_at, updated_at FROM tasks WHERE id = ?"),
    FIND_ALL("SELECT id, title, description, is_completed, created_at, updated_at FROM tasks"),
    FIND_ALL_BY_COMPLETED("SELECT id, title, description, is_completed, created_at, updated_at FROM tasks WHERE is_completed = ?"),
    DELETE("DELETE FROM tasks WHERE id = ?"),
    COUNT("SELECT COUNT(*) FROM tasks"),
    COUNT_BY_COMPLETED("SELECT COUNT(*) FROM tasks WHERE is_completed = ?");

    private final String query;

    SqlQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
