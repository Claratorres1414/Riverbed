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
class TwoPairStrategyTest {

    private final TwoPairStrategy strategy = new TwoPairStrategy();
    @Test
    void shouldRecognizeTwoPair() {
        List<Card> cards = List.of(
                createCard(Rank.ACE, Suit.SPADES),
                createCard(Rank.ACE, Suit.HEARTS),
                createCard(Rank.KING, Suit.SPADES),
                createCard(Rank.KING, Suit.HEARTS),
                createCard(Rank.QUEEN, Suit.SPADES),
                createCard(Rank.TEN, Suit.CLUBS),
                createCard(Rank.TWO, Suit.DIAMONDS)
        );
        Optional<HandEvaluation> result = strategy.evaluate(cards);
        assertTrue(result.isPresent());
        assertEquals(HandRank.TWO_PAIR, result.get().rank());
        assertEquals(5, result.get().bestFiveCards().size());
        assertEquals(
                List.of(
                        Rank.ACE.getStrength(),
                        Rank.KING.getStrength(),
                        Rank.QUEEN.getStrength()
                ),
                result.get().tieBreakValues()
        );
    }
    @Test
    void shouldSelectHighestTwoPairsWhenThreePairsExist() {
        List<Card> cards = List.of(
                createCard(Rank.ACE, Suit.SPADES),
                createCard(Rank.ACE, Suit.HEARTS),
                createCard(Rank.KING, Suit.SPADES),
                createCard(Rank.KING, Suit.HEARTS),
                createCard(Rank.QUEEN, Suit.SPADES),
                createCard(Rank.QUEEN, Suit.HEARTS),
                createCard(Rank.TWO, Suit.DIAMONDS)
        );
        Optional<HandEvaluation> result = strategy.evaluate(cards);
        assertTrue(result.isPresent());
        assertEquals(
                List.of(
                        Rank.ACE.getStrength(),
                        Rank.KING.getStrength(),
                        Rank.QUEEN.getStrength()
                ),
                result.get().tieBreakValues()
        );
        assertEquals(5, result.get().bestFiveCards().size());
    }
    @Test
    void shouldSelectHighestKicker() {
        List<Card> cards = List.of(
                createCard(Rank.ACE, Suit.SPADES),
                createCard(Rank.ACE, Suit.HEARTS),
                createCard(Rank.KING, Suit.SPADES),
                createCard(Rank.KING, Suit.HEARTS),
                createCard(Rank.QUEEN, Suit.SPADES),
                createCard(Rank.TEN, Suit.CLUBS),
                createCard(Rank.TWO, Suit.DIAMONDS)
        );
        Optional<HandEvaluation> result = strategy.evaluate(cards);
        assertTrue(result.isPresent());
        assertEquals(
                Rank.QUEEN.getStrength(),
                result.get().tieBreakValues().get(2)
        );
    }
    @Test
    void shouldReturnEmptyWhenThreeOfAKindExists() {
        List<Card> cards = List.of(
                createCard(Rank.ACE, Suit.SPADES),
                createCard(Rank.ACE, Suit.HEARTS),
                createCard(Rank.ACE, Suit.DIAMONDS),
                createCard(Rank.KING, Suit.SPADES),
                createCard(Rank.KING, Suit.HEARTS)
        );
        assertTrue(strategy.evaluate(cards).isEmpty());
    }
    @Test
    void shouldReturnEmptyWhenFourOfAKindExists() {
        List<Card> cards = List.of(
                createCard(Rank.ACE, Suit.SPADES),
                createCard(Rank.ACE, Suit.HEARTS),
                createCard(Rank.ACE, Suit.DIAMONDS),
                createCard(Rank.ACE, Suit.CLUBS),
                createCard(Rank.KING, Suit.SPADES)
        );
        assertTrue(strategy.evaluate(cards).isEmpty());
    }
    @Test
    void shouldReturnEmptyWhenOnlyOnePairExists() {
        List<Card> cards = List.of(
                createCard(Rank.ACE, Suit.SPADES),
                createCard(Rank.ACE, Suit.HEARTS),
                createCard(Rank.KING, Suit.SPADES),
                createCard(Rank.QUEEN, Suit.HEARTS),
                createCard(Rank.JACK, Suit.CLUBS)
        );
        assertTrue(strategy.evaluate(cards).isEmpty());
    }
    @Test
    void shouldReturnEmptyWhenNoPairExists() {
        List<Card> cards = List.of(
                createCard(Rank.ACE, Suit.SPADES),
                createCard(Rank.KING, Suit.HEARTS),
                createCard(Rank.QUEEN, Suit.DIAMONDS),
                createCard(Rank.JACK, Suit.CLUBS),
                createCard(Rank.NINE, Suit.SPADES)
        );
        assertTrue(strategy.evaluate(cards).isEmpty());
    }
    @Test
    void shouldReturnEmptyWhenLessThanFiveCardsAreProvided() {
        List<Card> cards = List.of(
                createCard(Rank.ACE, Suit.SPADES),
                createCard(Rank.ACE, Suit.HEARTS),
                createCard(Rank.KING, Suit.SPADES),
                createCard(Rank.KING, Suit.HEARTS)
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