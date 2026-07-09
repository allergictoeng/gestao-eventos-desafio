CREATE TABLE events (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    descricao VARCHAR(1000),
    data_hora TIMESTAMP NOT NULL,
    local VARCHAR(200) NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE INDEX idx_active_events_date ON events (data_hora) WHERE deleted = false;