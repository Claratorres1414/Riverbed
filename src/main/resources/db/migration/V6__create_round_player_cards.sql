CREATE TABLE round_player_cards (
    round_id UUID NOT NULL,
    player_id UUID NOT NULL,
    card_id BIGINT NOT NULL,
    position SMALLINT NOT NULL,

    CONSTRAINT pk_round_player_cards
        PRIMARY KEY (round_id, player_id, position),

    CONSTRAINT fk_round_player_cards_round
        FOREIGN KEY (round_id)
            REFERENCES rounds (id),

    CONSTRAINT fk_round_player_cards_player
        FOREIGN KEY (player_id)
            REFERENCES users (id),

    CONSTRAINT fk_round_player_cards_card
        FOREIGN KEY (card_id)
            REFERENCES cards (id),

    CONSTRAINT uk_round_player_cards_card
        UNIQUE (round_id, card_id),

    CONSTRAINT chk_round_player_cards_position
        CHECK (position IN (1, 2))
);

CREATE INDEX idx_round_player_cards_player
    ON round_player_cards (round_id, player_id);