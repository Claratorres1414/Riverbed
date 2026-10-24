package com.project.riverbed.domain.game.exceptions;

public class UnauthorizedGameStartException extends RuntimeException {
    public UnauthorizedGameStartException() {
        super("Somente o criador da partida pode iniciá-la");
    }
}
