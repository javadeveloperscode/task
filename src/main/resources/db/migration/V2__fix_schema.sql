-- Убираем NOT NULL с дат (для глобальных задач)
ALTER TABLE tasks ALTER COLUMN start_date DROP NOT NULL;
ALTER TABLE tasks ALTER COLUMN end_date DROP NOT NULL;

-- Меняем тип колонок с DATE на TIMESTAMP если нужно
ALTER TABLE tasks
    ALTER COLUMN start_date TYPE TIMESTAMP USING start_date::TIMESTAMP,
    ALTER COLUMN end_date   TYPE TIMESTAMP USING end_date::TIMESTAMP;

-- Добавляем колонку type если её нет
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS type VARCHAR(20) NOT NULL DEFAULT 'DAILY';

-- Создаём таблицу пользователей если нет
CREATE TABLE IF NOT EXISTS app_users (
    id       BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- Создаём таблицу remember-me токенов если нет
CREATE TABLE IF NOT EXISTS persistent_logins (
    username  VARCHAR(64) NOT NULL,
    series    VARCHAR(64) PRIMARY KEY,
    token     VARCHAR(64) NOT NULL,
    last_used TIMESTAMP   NOT NULL
);