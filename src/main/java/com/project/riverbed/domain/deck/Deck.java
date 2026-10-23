package com.project.riverbed.domain.deck;

import com.project.riverbed.domain.card.entitys.Card;
import com.project.riverbed.domain.card.enums.Rank;
import com.project.riverbed.domain.card.enums.Suit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Deck {

    private static final int DECK_SIZE = 52;

    private final List<Card> cards;

    public Deck(List<Card> cards) {
        validateDeck(cards);
        this.cards = new ArrayList<>(cards);
    }

    private void validateDeck(List<Card> cards) {
        if (cards == null || cards.size() != DECK_SIZE) {
            throw new IllegalArgumentException(
                    "O baralho deve ter exatamente 52 cartas."
            );
        }

        Set<String> combinations = new HashSet<>();

        for (Card card : cards) {
            if (card == null || card.getSuit() == null || card.getRank() == null) {
                throw new IllegalArgumentException(
                        "O baralho não pode conter cartas inválidas."
                );
            }

            String combination = card.getSuit().name() + "_" + card.getRank().name();

            if (!combinations.add(combination)) {
                throw new IllegalArgumentException(
                        "O baralho não pode conter cartas duplicadas."
                );
            }
        }

        if (combinations.size() != Suit.values().length * Rank.values().length) {
            throw new IllegalArgumentException(
                    "O baralho deve conter todas as combinações de naipe e valor."
            );
        }
    }

    public List<Card> getCards() {
        return List.copyOf(cards);
    }
}
