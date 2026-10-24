package com.project.riverbed.domain.game.dtos;

import com.project.riverbed.domain.game.GameStatus;

import java.util.UUID;

public record StartGameResponse(
        UUID gameId,
        GameStatus status
) {
}
