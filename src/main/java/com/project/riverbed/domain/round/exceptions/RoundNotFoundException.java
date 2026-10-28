package com.project.riverbed.domain.round.exceptions;

import java.util.UUID;

public class RoundNotFoundException extends RuntimeException {

    public RoundNotFoundException(UUID roundId) {
        super("Rodada não encontrada: " + roundId);
    }
}