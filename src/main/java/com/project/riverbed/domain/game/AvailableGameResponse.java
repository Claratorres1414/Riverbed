
package com.project.riverbed.domain.game;

import java.util.UUID;

public record AvailableGameResponse(
        UUID id,
        Long currentPlayers,
        Integer maxPlayers
) {
}
