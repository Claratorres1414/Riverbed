package com.project.riverbed.domain.hand.strategy;

import com.project.riverbed.domain.card.entitys.Card;
import com.project.riverbed.domain.card.enums.Rank;
import com.project.riverbed.domain.card.enums.Suit;
import com.project.riverbed.domain.hand.enums.HandRank;
import com.project.riverbed.domain.hand.result.HandEvaluation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StraightFlushStrategyTest {

    @Mock
    private StraightStrategy straightStrategy;
    private StraightFlushStrategy straightFlushStrategy;
    @BeforeEach
    void setUp() {
        straightFlushStrategy = new StraightFlushStrategy(straightStrategy);
    }
    @Test
    void shouldRecognizeStraightFlush() {
        List<Card> cards = List.of(
                createCard(Rank.FIVE, Suit.SPADES),
                createCard(Rank.SIX, Suit.SPADES),
                createCard(Rank.SEVEN, Suit.SPADES),
                createCard(Rank.EIGHT, Suit.SPADES),
                createCard(Rank.NINE, Suit.SPADES)
        );
        HandEvaluation straightEvaluation = new HandEvaluation(
                HandRank.STRAIGHT,
                List.of(9),
                cards
        );
        when(straightStrategy.evaluate(cards))
                .thenReturn(Optional.of(straightEvaluation));
        Optional<HandEvaluation> result = straightFlushStrategy.evaluate(cards);
        assertTrue(result.isPresent());
        assertEquals(HandRank.STRAIGHT_FLUSH, result.get().rank());
        assertEquals(List.of(9), result.get().tieBreakValues());
        assertEquals(cards, result.get().bestFiveCards());
        verify(straightStrategy).evaluate(cards);
    }
    @Test
    void shouldReturnEmptyWhenNoStraightExists() {
        List<Card> cards = List.of(
                createCard(Rank.TWO, Suit.SPADES),
                createCard(Rank.FOUR, Suit.SPADES),
                createCard(Rank.SIX, Suit.SPADES),
                createCard(Rank.EIGHT, Suit.SPADES),
                createCard(Rank.TEN, Suit.SPADES)
        );
        when(straightStrategy.evaluate(cards))
                .thenReturn(Optional.empty());
        Optional<HandEvaluation> result = straightFlushStrategy.evaluate(cards);
        assertTrue(result.isEmpty());
        verify(straightStrategy).evaluate(cards);
    }
    @Test
    void shouldReturnEmptyWhenNoSuitHasFiveCards() {
        List<Card> cards = List.of(
                createCard(Rank.TWO, Suit.SPADES),
                createCard(Rank.FOUR, Suit.SPADES),
                createCard(Rank.SIX, Suit.HEARTS),
                createCard(Rank.EIGHT, Suit.HEARTS),
                createCard(Rank.TEN, Suit.DIAMONDS)
        );
        Optional<HandEvaluation> result = straightFlushStrategy.evaluate(cards);
        assertTrue(result.isEmpty());
        verifyNoInteractions(straightStrategy);
    }
    @Test
    void shouldReturnEmptyWhenLessThanFiveCardsAreProvided() {
        List<Card> cards = List.of(
                createCard(Rank.FIVE, Suit.SPADES),
                createCard(Rank.SIX, Suit.SPADES),
                createCard(Rank.SEVEN, Suit.SPADES),
                createCard(Rank.EIGHT, Suit.SPADES)
        );
        Optional<HandEvaluation> result = straightFlushStrategy.evaluate(cards);
        assertTrue(result.isEmpty());
        verifyNoInteractions(straightStrategy);
    }
    private Card createCard(Rank rank, Suit suit) {
        return Card.builder()
                .rank(rank)
                .suit(suit)
                .build();
    }

}