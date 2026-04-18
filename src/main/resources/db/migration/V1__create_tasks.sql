CREATE TABLE IF NOT EXISTS tasks (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    start_date  TIMESTAMP,
    end_date    TIMESTAMP,
    status      VARCHAR(20)  NOT NULL DEFAULT 'TODO',
    type        VARCHAR(20)  NOT NULL DEFAULT 'DAILY'
);

CREATE TABLE IF NOT EXISTS app_users (
    id       BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS persistent_logins (
    username  VARCHAR(64) NOT NULL,
    series    VARCHAR(64) PRIMARY KEY,
    token     VARCHAR(64) NOT NULL,
    last_used TIMESTAMP   NOT NULL
);