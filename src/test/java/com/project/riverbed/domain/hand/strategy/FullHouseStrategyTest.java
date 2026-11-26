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
class FullHouseStrategyTest {

    private FullHouseStrategy fullHouseStrategy;

    @BeforeEach
    void setUp() {
        fullHouseStrategy = new FullHouseStrategy();
    }

    @Test
    void shouldRecognizeFullHouse() {
        List<Card> cards = List.of(
                createCard(Rank.ACE, Suit.SPADES),
                createCard(Rank.ACE, Suit.HEARTS),
                createCard(Rank.ACE, Suit.DIAMONDS),
                createCard(Rank.KING, Suit.SPADES),
                createCard(Rank.KING, Suit.HEARTS)
        );
        Optional<HandEvaluation> result = fullHouseStrategy.evaluate(cards);
        assertTrue(result.isPresent());
        assertEquals(HandRank.FULL_HOUSE, result.get().rank());
        assertEquals(List.of(14, 13), result.get().tieBreakValues());
        assertEquals(5, result.get().bestFiveCards().size());
    }
    @Test
    void shouldSelectHighestThreeOfAKind() {
        List<Card> cards = List.of(
                createCard(Rank.ACE, Suit.SPADES),
                createCard(Rank.ACE, Suit.HEARTS),
                createCard(Rank.ACE, Suit.DIAMONDS),
                createCard(Rank.KING, Suit.SPADES),
                createCard(Rank.KING, Suit.HEARTS),
                createCard(Rank.KING, Suit.DIAMONDS),
                createCard(Rank.QUEEN, Suit.SPADES)
        );
        Optional<HandEvaluation> result = fullHouseStrategy.evaluate(cards);
        assertTrue(result.isPresent());
        assertEquals(List.of(14, 13), result.get().tieBreakValues());
        assertEquals(
                3,
                result.get().bestFiveCards().stream()
                        .filter(card -> card.getRank() == Rank.ACE)
                        .count()
        );
        assertEquals(
                2,
                result.get().bestFiveCards().stream()
                        .filter(card -> card.getRank() == Rank.KING)
                        .count()
        );
    }
    @Test
    void shouldSelectHighestPair() {
        List<Card> cards = List.of(
                createCard(Rank.ACE, Suit.SPADES),
                createCard(Rank.ACE, Suit.HEARTS),
                createCard(Rank.ACE, Suit.DIAMONDS),
                createCard(Rank.KING, Suit.SPADES),
                createCard(Rank.KING, Suit.HEARTS),
                createCard(Rank.QUEEN, Suit.SPADES),
                createCard(Rank.QUEEN, Suit.HEARTS)
        );
        Optional<HandEvaluation> result = fullHouseStrategy.evaluate(cards);
        assertTrue(result.isPresent());
        assertEquals(List.of(14, 13), result.get().tieBreakValues());
        assertEquals(
                2,
                result.get().bestFiveCards().stream()
                        .filter(card -> card.getRank() == Rank.KING)
                        .count()
        );
        assertEquals(
                0,
                result.get().bestFiveCards().stream()
                        .filter(card -> card.getRank() == Rank.QUEEN)
                        .count()
        );
    }
    @Test
    void shouldReturnEmptyWhenNoThreeOfAKindExists() {
        List<Card> cards = List.of(
                createCard(Rank.ACE, Suit.SPADES),
                createCard(Rank.ACE, Suit.HEARTS),
                createCard(Rank.KING, Suit.DIAMONDS),
                createCard(Rank.KING, Suit.CLUBS),
                createCard(Rank.QUEEN, Suit.SPADES)
        );
        Optional<HandEvaluation> result = fullHouseStrategy.evaluate(cards);
        assertTrue(result.isEmpty());
    }
    @Test
    void shouldReturnEmptyWhenNoPairExists() {
        List<Card> cards = List.of(
                createCard(Rank.ACE, Suit.SPADES),
                createCard(Rank.ACE, Suit.HEARTS),
                createCard(Rank.ACE, Suit.DIAMONDS),
                createCard(Rank.KING, Suit.SPADES),
                createCard(Rank.QUEEN, Suit.HEARTS)
        );
        Optional<HandEvaluation> result = fullHouseStrategy.evaluate(cards);
        assertTrue(result.isEmpty());
    }
    @Test
    void shouldReturnEmptyWhenLessThanFiveCardsAreProvided() {
        List<Card> cards = List.of(
                createCard(Rank.ACE, Suit.SPADES),
                createCard(Rank.ACE, Suit.HEARTS),
                createCard(Rank.ACE, Suit.DIAMONDS),
                createCard(Rank.KING, Suit.SPADES)
        );
        Optional<HandEvaluation> result = fullHouseStrategy.evaluate(cards);
        assertTrue(result.isEmpty());
    }
    private Card createCard(Rank rank, Suit suit) {
        return Card.builder()
                .rank(rank)
                .suit(suit)
                .build();
    }

}