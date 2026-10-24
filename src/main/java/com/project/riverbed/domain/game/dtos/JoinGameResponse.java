package com.project.riverbed.domain.game.dtos;

import java.util.UUID;

public record JoinGameResponse(
    UUID gameId,
    UUID playerId
) {}