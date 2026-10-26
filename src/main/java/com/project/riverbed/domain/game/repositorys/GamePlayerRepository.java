package com.project.riverbed.domain.game.repositorys;

import com.project.riverbed.domain.game.GamePlayer;
import com.project.riverbed.domain.game.GamePlayerId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface GamePlayerRepository extends JpaRepository<GamePlayer, GamePlayerId> {

    @Query("""
        SELECT gp.player.id
        FROM GamePlayer gp
        WHERE gp.game.id = : gameId
        """)
    List<UUID> findPlayerIdsByGameId(@Param("gameId") UUID gameId);
}
