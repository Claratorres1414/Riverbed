
package com.project.riverbed.domain.game;

import com.project.riverbed.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "game_players")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GamePlayer {

    @EmbeddedId
    private GamePlayerId id;

    @MapsId("gameId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @MapsId("playerId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private User player;

    @CreationTimestamp
    @Column(name = "joined_at", nullable = false, updatable = false)
    private OffsetDateTime joinedAt;
}
