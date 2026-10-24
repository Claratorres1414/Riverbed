package com.project.riverbed.domain.game.exceptions;

public class InsufficientPlayersException extends RuntimeException {
    public InsufficientPlayersException() {
        super("A partida precisa ter pelo menos dois jogadores para ser iniciada");
    }
}
