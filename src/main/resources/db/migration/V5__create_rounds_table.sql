CREATE TABLE rounds (
    id UUID PRIMARY KEY,

    game_id UUID NOT NULL,

    status VARCHAR(20) NOT NULL DEFAULT 'EM_ANDAMENTO',

    cards_distributed BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_rounds_game
    FOREIGN KEY (game_id)
        REFERENCES games (id),

    CONSTRAINT chk_rounds_status
    CHECK (status IN ('EM_ANDAMENTO', 'ENCERRADA'))
);

CREATE INDEX idx_rounds_game_id
    ON rounds (game_id);

CREATE UNIQUE INDEX idx_rounds_one_active_per_game
    ON rounds (game_id)
    WHERE status = 'EM_ANDAMENTO';