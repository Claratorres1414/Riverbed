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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class StraightStrategyTest {
    private StraightStrategy straightStrategy;

    @BeforeEach
    void setUp() {
        straightStrategy = new StraightStrategy();
    }

    @Test
    void shouldRecognizeStraight() {

        List<Card> cards = List.of(
                createCard(Rank.FIVE, Suit.SPADES),
                createCard(Rank.SIX, Suit.HEARTS),
                createCard(Rank.SEVEN, Suit.DIAMONDS),
                createCard(Rank.EIGHT, Suit.CLUBS),
                createCard(Rank.NINE, Suit.SPADES)
        );

        Optional<HandEvaluation> result = straightStrategy.evaluate(cards);

        assertTrue(result.isPresent());
        assertEquals(HandRank.STRAIGHT, result.get().rank());
        assertEquals(List.of(9), result.get().tieBreakValues());
        assertEquals(5, result.get().bestFiveCards().size());
    }

    @Test
    void shouldRecognizeHighestStraight() {

        List<Card> cards = List.of(
                createCard(Rank.TEN, Suit.SPADES),
                createCard(Rank.JACK, Suit.HEARTS),
                createCard(Rank.QUEEN, Suit.DIAMONDS),
                createCard(Rank.KING, Suit.CLUBS),
                createCard(Rank.ACE, Suit.SPADES)
        );

        Optional<HandEvaluation> result = straightStrategy.evaluate(cards);

        assertTrue(result.isPresent());
        assertEquals(List.of(14), result.get().tieBreakValues());
        assertEquals(5, result.get().bestFiveCards().size());
    }

    @Test
    void shouldRecognizeWheelStraight() {

        List<Card> cards = List.of(
                createCard(Rank.ACE, Suit.SPADES),
                createCard(Rank.TWO, Suit.HEARTS),
                createCard(Rank.THREE, Suit.DIAMONDS),
                createCard(Rank.FOUR, Suit.CLUBS),
                createCard(Rank.FIVE, Suit.SPADES)
        );

        Optional<HandEvaluation> result = straightStrategy.evaluate(cards);

        assertTrue(result.isPresent());
        assertEquals(List.of(5), result.get().tieBreakValues());
        assertEquals(5, result.get().bestFiveCards().size());
    }

    @Test
    void shouldSelectHighestStraightWhenMultipleSequencesExist() {

        List<Card> cards = List.of(
                createCard(Rank.FIVE, Suit.SPADES),
                createCard(Rank.SIX, Suit.HEARTS),
                createCard(Rank.SEVEN, Suit.DIAMONDS),
                createCard(Rank.EIGHT, Suit.CLUBS),
                createCard(Rank.NINE, Suit.SPADES),
                createCard(Rank.TEN, Suit.HEARTS),
                createCard(Rank.JACK, Suit.DIAMONDS)
        );

        Optional<HandEvaluation> result = straightStrategy.evaluate(cards);

        assertTrue(result.isPresent());
        assertEquals(List.of(11), result.get().tieBreakValues());
    }

    @Test
    void shouldIgnoreDuplicateRanksWhenEvaluatingStraight() {

        List<Card> cards = List.of(
                createCard(Rank.FIVE, Suit.SPADES),
                createCard(Rank.FIVE, Suit.HEARTS),
                createCard(Rank.SIX, Suit.DIAMONDS),
                createCard(Rank.SEVEN, Suit.CLUBS),
                createCard(Rank.EIGHT, Suit.SPADES),
                createCard(Rank.NINE, Suit.HEARTS)
        );

        Optional<HandEvaluation> result = straightStrategy.evaluate(cards);

        assertTrue(result.isPresent());
        assertEquals(List.of(9), result.get().tieBreakValues());
        assertEquals(5, result.get().bestFiveCards().size());
    }

    @Test
    void shouldReturnEmptyWhenNoStraightExists() {

        List<Card> cards = List.of(
                createCard(Rank.TWO, Suit.SPADES),
                createCard(Rank.FOUR, Suit.HEARTS),
                createCard(Rank.SIX, Suit.DIAMONDS),
                createCard(Rank.EIGHT, Suit.CLUBS),
                createCard(Rank.TEN, Suit.SPADES)
        );

        Optional<HandEvaluation> result = straightStrategy.evaluate(cards);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenLessThanFiveCardsAreProvided() {

        List<Card> cards = List.of(
                createCard(Rank.FIVE, Suit.SPADES),
                createCard(Rank.SIX, Suit.HEARTS),
                createCard(Rank.SEVEN, Suit.DIAMONDS),
                createCard(Rank.EIGHT, Suit.CLUBS)
        );

        Optional<HandEvaluation> result = straightStrategy.evaluate(cards);

        assertTrue(result.isEmpty());
    }

    private Card createCard(Rank rank, Suit suit) {
        return Card.builder()
                .rank(rank)
                .suit(suit)
                .build();
    }
}
