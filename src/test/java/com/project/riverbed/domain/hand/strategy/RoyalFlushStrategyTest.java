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
class RoyalFlushStrategyTest {

    private RoyalFlushStrategy royalFlushStrategy;
    @BeforeEach
    void setUp() {
        royalFlushStrategy = new RoyalFlushStrategy();
    }
    @Test
    void shouldRecognizeRoyalFlush() {
        List<Card> cards = List.of(
                createCard(Rank.TEN, Suit.SPADES),
                createCard(Rank.JACK, Suit.SPADES),
                createCard(Rank.QUEEN, Suit.SPADES),
                createCard(Rank.KING, Suit.SPADES),
                createCard(Rank.ACE, Suit.SPADES)
        );
        Optional<HandEvaluation> result = royalFlushStrategy.evaluate(cards);
        assertTrue(result.isPresent());
        assertEquals(HandRank.ROYAL_FLUSH, result.get().rank());
        assertEquals(List.of(14), result.get().tieBreakValues());
        assertEquals(5, result.get().bestFiveCards().size());
        assertTrue(result.get().bestFiveCards().containsAll(cards));
    }
    @Test
    void shouldReturnEmptyWhenCardsDoNotFormRoyalFlush() {
        List<Card> cards = List.of(
                createCard(Rank.TEN, Suit.SPADES),
                createCard(Rank.JACK, Suit.HEARTS),
                createCard(Rank.QUEEN, Suit.SPADES),
                createCard(Rank.KING, Suit.SPADES),
                createCard(Rank.ACE, Suit.SPADES)
        );
        Optional<HandEvaluation> result = royalFlushStrategy.evaluate(cards);
        assertTrue(result.isEmpty());
    }
    @Test
    void shouldReturnEmptyWhenCardsFormStraightFlushButNotRoyalFlush() {
        List<Card> cards = List.of(
                createCard(Rank.FIVE, Suit.SPADES),
                createCard(Rank.SIX, Suit.SPADES),
                createCard(Rank.SEVEN, Suit.SPADES),
                createCard(Rank.EIGHT, Suit.SPADES),
                createCard(Rank.NINE, Suit.SPADES)
        );
        Optional<HandEvaluation> result = royalFlushStrategy.evaluate(cards);
        assertTrue(result.isEmpty());
    }
    @Test
    void shouldReturnEmptyWhenLessThanFiveCardsAreProvided() {
        List<Card> cards = List.of(
                createCard(Rank.TEN, Suit.SPADES),
                createCard(Rank.JACK, Suit.SPADES),
                createCard(Rank.QUEEN, Suit.SPADES),
                createCard(Rank.KING, Suit.SPADES)
        );
        Optional<HandEvaluation> result = royalFlushStrategy.evaluate(cards);
        assertTrue(result.isEmpty());
    }
    private Card createCard(Rank rank, Suit suit) {
        return Card.builder()
                .rank(rank)
                .suit(suit)
                .build();
    }

}