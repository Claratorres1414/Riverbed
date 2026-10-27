package com.project.riverbed.domain.round.repositorys;

import com.project.riverbed.domain.round.entitys.RoundPlayerCard;
import com.project.riverbed.domain.round.entitys.RoundPlayerCardId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RoundPlayerCardRepository
        extends JpaRepository<RoundPlayerCard, RoundPlayerCardId> {

    List<RoundPlayerCard> findByRoundIdAndPlayerIdOrderByPosition(
            UUID roundId,
            UUID playerId
    );
}