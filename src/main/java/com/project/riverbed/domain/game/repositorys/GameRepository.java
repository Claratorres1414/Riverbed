
package com.project.riverbed.domain.game.repositorys;

import com.project.riverbed.domain.game.AvailableGameProjection;
import com.project.riverbed.domain.game.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface GameRepository extends JpaRepository<Game, UUID> {

    @Query(value = """
            SELECT
                g.id AS id,
                COUNT(gp.player_id) AS "currentPlayers",
                g.max_players AS "maxPlayers"
            FROM games g
            LEFT JOIN game_players gp
                ON gp.game_id = g.id
            WHERE g.status = 'AGUARDANDO_JOGADORES'
            GROUP BY g.id, g.max_players
            HAVING COUNT(gp.player_id) < g.max_players
            ORDER BY g.created_at ASC
            """, nativeQuery = true)
    List<AvailableGameProjection> findAvailableGames();

    @Query("""
        SELECT COUNT(gp)
        FROM GamePlayer gp
        WHERE gp.game.id = :gameId
        """)
    long countPlayersByGameId(@Param("gameId") UUID gameId);

    @Query("""
        SELECT COUNT(gp) > 0
        FROM GamePlayer gp
        WHERE gp.game.id = :gameId AND gp.player.id = :playerId
        """)
    boolean existsByGameIdAndPlayerId(@Param("gameId") UUID gameId, @Param("playerId") UUID playerId);
}