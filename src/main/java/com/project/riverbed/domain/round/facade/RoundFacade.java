package com.project.riverbed.domain.round.facade;

import com.project.riverbed.domain.round.dtos.PrivateCardsResponse;
import com.project.riverbed.domain.round.dtos.RoundStartResponse;
import com.project.riverbed.domain.round.dtos.TableCardsResponse;
import com.project.riverbed.domain.round.services.RoundService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RoundFacade {
    private final RoundService roundService;

    public RoundStartResponse startRound(UUID gameId) {
        return roundService.startRound(gameId);
    }

    public PrivateCardsResponse getPrivateCards(UUID roundId, UUID playerId) {
        return roundService.getPrivateCards(roundId, playerId);
    }

    public TableCardsResponse tempDistributeTableCards(UUID roundId) {
        return roundService.tempDistributeTableCards(roundId);
    }

    public TableCardsResponse getAllTableCards(UUID roundId) {
        return roundService.getAllTableCards(roundId);
    }

    public void endRound(UUID roundId) {
        roundService.endRound(roundId);
    }
}
