package com.project.riverbed.domain.player.exception;

public class PlayerNotFoundException extends RuntimeException {
    
    public PlayerNotFoundException() {
        super("Jogador não encontrado.");
    }

    public PlayerNotFoundException(String message) {
        super(message);
    }
}