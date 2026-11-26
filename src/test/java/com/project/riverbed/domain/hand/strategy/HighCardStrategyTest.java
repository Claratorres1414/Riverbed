package com.project.riverbed.domain.hand.strategy;

import com.project.riverbed.domain.card.entitys.Card;
import com.project.riverbed.domain.card.enums.Rank;
import com.project.riverbed.domain.card.enums.Suit;
import com.project.riverbed.domain.hand.enums.HandRank;
import com.project.riverbed.domain.hand.result.HandEvaluation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class HighCardStrategyTest {

    private final HighCardStrategy strategy = new HighCardStrategy();

    @Test
    void shouldRecognizeHighCard() {
        List<Card> cards = List.of(
                createCard(Rank.ACE, Suit.SPADES),
                createCard(Rank.KING, Suit.HEARTS),
                createCard(Rank.QUEEN, Suit.DIAMONDS),
                createCard(Rank.JACK, Suit.CLUBS),
                createCard(Rank.NINE, Suit.SPADES)
        );

        Optional<HandEvaluation> result = strategy.evaluate(cards);

        assertTrue(result.isPresent());
        assertEquals(HandRank.HIGH_CARD, result.get().rank());
        assertEquals(5, result.get().bestFiveCards().size());

        assertEquals(
                List.of(
                        Rank.ACE.getStrength(),
                        Rank.KING.getStrength(),
                        Rank.QUEEN.getStrength(),
                        Rank.JACK.getStrength(),
                        Rank.NINE.getStrength()
                ),
                result.get().tieBreakValues()
        );
    }

    @Test
    void shouldSelectHighestFiveCards() {
        List<Card> cards = List.of(
                createCard(Rank.TWO, Suit.SPADES),
                createCard(Rank.ACE, Suit.HEARTS),
                createCard(Rank.FIVE, Suit.DIAMONDS),
                createCard(Rank.KING, Suit.CLUBS),
                createCard(Rank.THREE, Suit.SPADES),
                createCard(Rank.QUEEN, Suit.HEARTS),
                createCard(Rank.JACK, Suit.DIAMONDS)
        );

        Optional<HandEvaluation> result = strategy.evaluate(cards);

        assertTrue(result.isPresent());

        assertEquals(
                List.of(
                        Rank.ACE.getStrength(),
                        Rank.KING.getStrength(),
                        Rank.QUEEN.getStrength(),
                        Rank.JACK.getStrength(),
                        Rank.FIVE.getStrength()
                ),
                result.get().tieBreakValues()
        );

        assertEquals(5, result.get().bestFiveCards().size());
    }

    @Test
    void shouldOrderTieBreakValuesFromHighestToLowest() {
        List<Card> cards = List.of(
                createCard(Rank.NINE, Suit.SPADES),
                createCard(Rank.ACE, Suit.HEARTS),
                createCard(Rank.JACK, Suit.DIAMONDS),
                createCard(Rank.KING, Suit.CLUBS),
                createCard(Rank.QUEEN, Suit.SPADES)
        );

        Optional<HandEvaluation> result = strategy.evaluate(cards);

        assertTrue(result.isPresent());

        assertEquals(
                List.of(
                        Rank.ACE.getStrength(),
                        Rank.KING.getStrength(),
                        Rank.QUEEN.getStrength(),
                        Rank.JACK.getStrength(),
                        Rank.NINE.getStrength()
                ),
                result.get().tieBreakValues()
        );
    }

    @Test
    void shouldReturnEmptyWhenLessThanFiveCardsAreProvided() {
        List<Card> cards = List.of(
                createCard(Rank.ACE, Suit.SPADES),
                createCard(Rank.KING, Suit.HEARTS),
                createCard(Rank.QUEEN, Suit.DIAMONDS),
                createCard(Rank.JACK, Suit.CLUBS)
        );

        assertTrue(strategy.evaluate(cards).isEmpty());
    }

    private Card createCard(Rank rank, Suit suit) {
        return Card.builder()
                .rank(rank)
                .suit(suit)
                .build();
    }
}