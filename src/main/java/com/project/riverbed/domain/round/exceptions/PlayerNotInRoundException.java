package com.project.riverbed.domain.round.exceptions;

import java.util.UUID;

public class PlayerNotInRoundException extends RuntimeException {

    public PlayerNotInRoundException(UUID playerId, UUID roundId) {
        super("Jogador " + playerId + " não participa da rodada " + roundId);
    }
}