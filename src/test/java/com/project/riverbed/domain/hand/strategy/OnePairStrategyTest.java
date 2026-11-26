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
class OnePairStrategyTest {

    private final OnePairStrategy strategy = new OnePairStrategy();
    @Test
    void shouldRecognizeOnePair() {
        List<Card> cards = List.of(
                createCard(Rank.ACE, Suit.SPADES),
                createCard(Rank.ACE, Suit.HEARTS),
                createCard(Rank.KING, Suit.SPADES),
                createCard(Rank.QUEEN, Suit.HEARTS),
                createCard(Rank.JACK, Suit.CLUBS),
                createCard(Rank.TWO, Suit.DIAMONDS)
        );
        Optional<HandEvaluation> result = strategy.evaluate(cards);
        assertTrue(result.isPresent());
        assertEquals(HandRank.ONE_PAIR, result.get().rank());
        assertEquals(5, result.get().bestFiveCards().size());
        assertEquals(
                List.of(
                        Rank.ACE.getStrength(),
                        Rank.KING.getStrength(),
                        Rank.QUEEN.getStrength(),
                        Rank.JACK.getStrength()
                ),
                result.get().tieBreakValues()
        );
    }
    @Test
    void shouldSelectHighestKickers() {
        List<Card> cards = List.of(
                createCard(Rank.ACE, Suit.SPADES),
                createCard(Rank.ACE, Suit.HEARTS),
                createCard(Rank.KING, Suit.SPADES),
                createCard(Rank.QUEEN, Suit.HEARTS),
                createCard(Rank.JACK, Suit.CLUBS),
                createCard(Rank.NINE, Suit.DIAMONDS),
                createCard(Rank.TWO, Suit.CLUBS)
        );
        Optional<HandEvaluation> result = strategy.evaluate(cards);
        assertTrue(result.isPresent());
        assertEquals(
                List.of(
                        Rank.ACE.getStrength(),
                        Rank.KING.getStrength(),
                        Rank.QUEEN.getStrength(),
                        Rank.JACK.getStrength()
                ),
                result.get().tieBreakValues()
        );
        assertEquals(5, result.get().bestFiveCards().size());
    }
    @Test
    void shouldSelectHighestPairWhenPairAndOtherCardsExist() {
        List<Card> cards = List.of(
                createCard(Rank.TWO, Suit.SPADES),
                createCard(Rank.TWO, Suit.HEARTS),
                createCard(Rank.ACE, Suit.SPADES),
                createCard(Rank.KING, Suit.HEARTS),
                createCard(Rank.QUEEN, Suit.CLUBS),
                createCard(Rank.JACK, Suit.DIAMONDS),
                createCard(Rank.NINE, Suit.CLUBS)
        );
        Optional<HandEvaluation> result = strategy.evaluate(cards);
        assertTrue(result.isPresent());
        assertEquals(
                List.of(
                        Rank.TWO.getStrength(),
                        Rank.ACE.getStrength(),
                        Rank.KING.getStrength(),
                        Rank.QUEEN.getStrength()
                ),
                result.get().tieBreakValues()
        );
    }
    @Test
    void shouldReturnEmptyWhenTwoPairsExist() {
        List<Card> cards = List.of(
                createCard(Rank.ACE, Suit.SPADES),
                createCard(Rank.ACE, Suit.HEARTS),
                createCard(Rank.KING, Suit.SPADES),
                createCard(Rank.KING, Suit.HEARTS),
                createCard(Rank.QUEEN, Suit.CLUBS)
        );
        assertTrue(strategy.evaluate(cards).isEmpty());
    }
    @Test
    void shouldReturnEmptyWhenThreeOfAKindExists() {
        List<Card> cards = List.of(
                createCard(Rank.ACE, Suit.SPADES),
                createCard(Rank.ACE, Suit.HEARTS),
                createCard(Rank.ACE, Suit.DIAMONDS),
                createCard(Rank.KING, Suit.SPADES),
                createCard(Rank.QUEEN, Suit.HEARTS)
        );
        assertTrue(strategy.evaluate(cards).isEmpty());
    }
    @Test
    void shouldReturnEmptyWhenFullHouseExists() {
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
                createCard(Rank.QUEEN, Suit.HEARTS)
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
