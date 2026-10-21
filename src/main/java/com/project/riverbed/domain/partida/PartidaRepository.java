
package com.project.riverbed.domain.partida;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface PartidaRepository extends JpaRepository<Partida, UUID> {

    @Query(value = """
        SELECT
            p.id AS id,
            COUNT(pj.jogador_id) AS "jogadoresAtuais",
            p.max_participantes AS "maxParticipantes"
        FROM partidas p
        LEFT JOIN partida_jogadores pj
            ON pj.partida_id = p.id
        WHERE p.status = 'AGUARDANDO_JOGADORES'
        GROUP BY p.id, p.max_participantes
        HAVING COUNT(pj.jogador_id) < p.max_participantes
        ORDER BY p.created_at ASC
        """, nativeQuery = true)
    List<PartidaDisponivelProjection> buscarPartidasDisponiveis();
}
