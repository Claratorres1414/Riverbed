package com.project.riverbed.domain.deck;

import com.project.riverbed.domain.card.entitys.Card;
import com.project.riverbed.domain.card.enums.Rank;
import com.project.riverbed.domain.card.enums.Suit;

import java.security.SecureRandom;
import java.util.*;

public class Deck {

    private static final int DECK_SIZE = 52;

    private static final SecureRandom RANDOM = new SecureRandom();

    private final List<Card> cards;

    public Deck(List<Card> cards) {
        validateDeck(cards);
        this.cards = new ArrayList<>(cards);
        shuffle();
    }

    public List<Card> getCards() {
        return List.copyOf(cards);
    }

    public Card draw() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("Não há cartas disponíveis no baralho.");
        }

        return cards.removeLast();
    }

    public List<Card> draw(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade de cartas deve ser maior que zero."
            );
        }

        if (quantity > cards.size()) {
            throw new IllegalStateException(
                    "Não há cartas suficientes no baralho."
            );
        }

        List<Card> drawnCards = new ArrayList<>(quantity);

        for (int i = 0; i < quantity; i++) {
            drawnCards.add(draw());
        }

        return List.copyOf(drawnCards);
    }

    public int remainingCards() {
        return cards.size();
    }

    private void shuffle() {
        Collections.shuffle(cards, RANDOM);
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
}
