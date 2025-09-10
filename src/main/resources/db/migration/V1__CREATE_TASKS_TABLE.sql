CREATE TABLE tasks
(
    id           BIGSERIAL PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    description  TEXT,
    is_completed VARCHAR(50)  NOT NULL,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP,
    CONSTRAINT check_is_completed CHECK (is_completed IN ('NOT_COMPLETED', 'COMPLETED'))
);

-- Индекс для ускорения поиска по статусу выполнения
CREATE INDEX idx_tasks_is_completed ON tasks (is_completed);

-- Комментарии к столбцам для документации
COMMENT ON TABLE tasks IS 'Таблица для хранения задач';
COMMENT ON COLUMN tasks.id IS 'Уникальный идентификатор задачи';
COMMENT ON COLUMN tasks.title IS 'Заголовок задачи';
COMMENT ON COLUMN tasks.description IS 'Описание задачи';
COMMENT ON COLUMN tasks.is_completed IS 'Статус выполнения задачи (NOT_COMPLETED, COMPLETED)';
COMMENT ON COLUMN tasks.created_at IS 'Дата и время создания задачи';
COMMENT ON COLUMN tasks.updated_at IS 'Дата и время последнего обновления задачи';