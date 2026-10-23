
CREATE TABLE games (
                       id UUID PRIMARY KEY,
                       creator_id UUID NOT NULL,
                       max_players INTEGER NOT NULL,
                       status VARCHAR(30) NOT NULL,
                       created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                       CONSTRAINT fk_games_creator
                           FOREIGN KEY (creator_id)
                               REFERENCES users(id),

                       CONSTRAINT chk_games_max_players
                           CHECK (max_players >= 2),

                       CONSTRAINT chk_games_status
                           CHECK (
                               status IN (
                                          'AGUARDANDO_JOGADORES',
                                          'EM_ANDAMENTO',
                                          'ENCERRADA'
                                   )
                               )
);

CREATE TABLE game_players (
                              game_id UUID NOT NULL,
                              player_id UUID NOT NULL,
                              joined_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                              CONSTRAINT pk_game_players
                                  PRIMARY KEY (game_id, player_id),

                              CONSTRAINT fk_game_players_game
                                  FOREIGN KEY (game_id)
                                      REFERENCES games(id),

                              CONSTRAINT fk_game_players_user
                                  FOREIGN KEY (player_id)
                                      REFERENCES users(id)
);

CREATE INDEX idx_games_status
    ON games(status);

CREATE INDEX idx_game_players_player
    ON game_players(player_id);
