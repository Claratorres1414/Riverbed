package com.project.riverbed.domain.deck.services;

import com.project.riverbed.domain.card.entitys.Card;
import com.project.riverbed.domain.card.repositorys.CardRepository;
import com.project.riverbed.domain.deck.entitys.Deck;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardDistributionService {

    private static final int CARDS_PER_PLAYER = 2;

    private final CardRepository cardRepository;

    public Map<UUID, List<Card>> distribute(List<UUID> playerIds) {
        Deck deck = new Deck(cardRepository.findAll());

        Map<UUID, List<Card>> distribution = new HashMap<>();

        for (UUID playerId : playerIds) {
            distribution.put(playerId, deck.draw(CARDS_PER_PLAYER));
        }

        return Map.copyOf(distribution);
    }
}
