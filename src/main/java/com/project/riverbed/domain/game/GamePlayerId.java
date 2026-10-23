
package com.project.riverbed.domain.game;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class GamePlayerId implements Serializable {

    @Column(name = "game_id")
    private UUID gameId;

    @Column(name = "player_id")
    private UUID playerId;
}
