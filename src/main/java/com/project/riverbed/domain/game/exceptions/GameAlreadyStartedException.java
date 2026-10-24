package com.project.riverbed.domain.game.exceptions;

public class GameAlreadyStartedException extends RuntimeException {
    public GameAlreadyStartedException() {
        super("A partida já foi iniciada ou não está aguardando jogadores");
    }
}
