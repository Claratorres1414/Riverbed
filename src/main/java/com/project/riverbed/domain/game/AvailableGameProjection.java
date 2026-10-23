
package com.project.riverbed.domain.game;

import java.util.UUID;

public interface AvailableGameProjection {

    UUID getId();

    Long getCurrentPlayers();

    Integer getMaxPlayers();
}
