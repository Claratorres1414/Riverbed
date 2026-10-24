package com.project.riverbed.domain.game.exceptions;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException() {
        super("Partida não encontrada");
    }
}
