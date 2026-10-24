package com.project.riverbed.domain.game.dtos;

import java.util.UUID;

public record StartGameRequest(
        UUID playerId
) {
}
