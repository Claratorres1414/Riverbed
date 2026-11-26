package com.project.riverbed.domain.hand.strategy;

import com.project.riverbed.domain.card.entitys.Card;
import com.project.riverbed.domain.card.enums.Rank;
import com.project.riverbed.domain.card.enums.Suit;
import com.project.riverbed.domain.hand.enums.HandRank;
import com.project.riverbed.domain.hand.result.HandEvaluation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class FlushStrategyTest {
    private FlushStrategy flushStrategy;

    @BeforeEach
    void setUp() {
        flushStrategy = new FlushStrategy();
    }

    @Test
    void shouldRecognizeFlush() {

        List<Card> cards = List.of(
                createCard(Rank.ACE, Suit.SPADES),
                createCard(Rank.KING, Suit.SPADES),
                createCard(Rank.QUEEN, Suit.SPADES),
                createCard(Rank.JACK, Suit.SPADES),
                createCard(Rank.TEN, Suit.SPADES)
        );

        Optional<HandEvaluation> result = flushStrategy.evaluate(cards);

        assertTrue(result.isPresent());
        assertEquals(HandRank.FLUSH, result.get().rank());
        assertEquals(
                List.of(14, 13, 12, 11, 10),
                result.get().tieBreakValues()
        );
        assertEquals(5, result.get().bestFiveCards().size());
    }

    @Test
    void shouldSelectHighestFiveCardsWhenMoreThanFiveCardsShareSameSuit() {

        List<Card> cards = List.of(
                createCard(Rank.ACE, Suit.SPADES),
                createCard(Rank.KING, Suit.SPADES),
                createCard(Rank.QUEEN, Suit.SPADES),
                createCard(Rank.JACK, Suit.SPADES),
                createCard(Rank.TEN, Suit.SPADES),
                createCard(Rank.NINE, Suit.SPADES),
                createCard(Rank.EIGHT, Suit.SPADES)
        );

        Optional<HandEvaluation> result = flushStrategy.evaluate(cards);

        assertTrue(result.isPresent());

        assertEquals(
                List.of(14, 13, 12, 11, 10),
                result.get().tieBreakValues()
        );

        assertEquals(5, result.get().bestFiveCards().size());

        assertFalse(result.get().bestFiveCards().stream()
                .anyMatch(card ->
                        card.getRank() == Rank.NINE ||
                                card.getRank() == Rank.EIGHT
                ));
    }

    @Test
    void shouldReturnEmptyWhenNoFlushExists() {

        List<Card> cards = List.of(
                createCard(Rank.ACE, Suit.SPADES),
                createCard(Rank.KING, Suit.HEARTS),
                createCard(Rank.QUEEN, Suit.DIAMONDS),
                createCard(Rank.JACK, Suit.CLUBS),
                createCard(Rank.TEN, Suit.SPADES)
        );

        Optional<HandEvaluation> result = flushStrategy.evaluate(cards);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenLessThanFiveCardsAreProvided() {

        List<Card> cards = List.of(
                createCard(Rank.ACE, Suit.SPADES),
                createCard(Rank.KING, Suit.SPADES),
                createCard(Rank.QUEEN, Suit.SPADES),
                createCard(Rank.JACK, Suit.SPADES)
        );

        Optional<HandEvaluation> result = flushStrategy.evaluate(cards);

        assertTrue(result.isEmpty());
    }

    private Card createCard(Rank rank, Suit suit) {
        return Card.builder()
                .rank(rank)
                .suit(suit)
                .build();
    }
}
