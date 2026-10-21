
CREATE TABLE partidas (
    id UUID PRIMARY KEY,
    criador_id UUID NOT NULL,
    max_participantes INTEGER NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_partidas_criador
        FOREIGN KEY (criador_id)
        REFERENCES users(id),

    CONSTRAINT chk_partidas_max_participantes
        CHECK (max_participantes >= 2),

    CONSTRAINT chk_partidas_status
        CHECK (
            status IN (
                'AGUARDANDO_JOGADORES',
                'EM_ANDAMENTO',
                'ENCERRADA'
            )
        )
);

CREATE TABLE partida_jogadores (
    partida_id UUID NOT NULL,
    jogador_id UUID NOT NULL,
    joined_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_partida_jogadores
        PRIMARY KEY (partida_id, jogador_id),

    CONSTRAINT fk_partida_jogadores_partida
        FOREIGN KEY (partida_id)
        REFERENCES partidas(id),

    CONSTRAINT fk_partida_jogadores_usuario
        FOREIGN KEY (jogador_id)
        REFERENCES users(id)
);

CREATE INDEX idx_partidas_status
    ON partidas(status);

CREATE INDEX idx_partida_jogadores_jogador
    ON partida_jogadores(jogador_id);
