package com.project.riverbed.domain.game.exceptions;

public class GameFullException extends RuntimeException {
    public GameFullException() {
        super("A partida já atingiu a capacidade máxima de jogadores.");
    }
}