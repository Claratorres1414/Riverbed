package com.project.riverbed.domain.game.exceptions;

public class PlayerAlreadyInGameException extends RuntimeException {
    public PlayerAlreadyInGameException() {
        super("O jogador já está nesta partida.");
    }
}