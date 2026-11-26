package com.project.riverbed.domain.hand.service;

import com.project.riverbed.domain.card.entitys.Card;
import com.project.riverbed.domain.card.enums.Rank;
import com.project.riverbed.domain.card.enums.Suit;
import com.project.riverbed.domain.hand.enums.HandRank;
import com.project.riverbed.domain.hand.result.HandEvaluation;
import com.project.riverbed.domain.hand.strategy.HandEvaluationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HandEvaluatorServiceTest {

    @Mock
    private HandEvaluationStrategy highCardStrategy;

    @Mock
    private HandEvaluationStrategy onePairStrategy;

    @Mock
    private HandEvaluationStrategy royalFlushStrategy;

    private HandEvaluatorService handEvaluatorService;

    @BeforeEach
    void setUp() {
        handEvaluatorService = new HandEvaluatorService(
                List.of(
                        highCardStrategy,
                        onePairStrategy,
                        royalFlushStrategy
                )
        );
    }

    @Test
    void shouldReturnHighestRankEvaluation() {

        List<Card> cards = List.of(
                createCard(Rank.ACE, Suit.SPADES),
                createCard(Rank.KING, Suit.SPADES),
                createCard(Rank.QUEEN, Suit.SPADES),
                createCard(Rank.JACK, Suit.SPADES),
                createCard(Rank.TEN, Suit.SPADES)
        );

        HandEvaluation royalFlushEvaluation = new HandEvaluation(
                HandRank.ROYAL_FLUSH,
                List.of(14),
                cards
        );

        when(highCardStrategy.getRank()).thenReturn(HandRank.HIGH_CARD);
        when(onePairStrategy.getRank()).thenReturn(HandRank.ONE_PAIR);
        when(royalFlushStrategy.getRank()).thenReturn(HandRank.ROYAL_FLUSH);

        when(royalFlushStrategy.evaluate(cards))
                .thenReturn(Optional.of(royalFlushEvaluation));

        HandEvaluation result = handEvaluatorService.evaluate(cards);

        assertEquals(HandRank.ROYAL_FLUSH, result.rank());

        verify(royalFlushStrategy).evaluate(cards);

        verify(highCardStrategy, never()).evaluate(cards);
        verify(onePairStrategy, never()).evaluate(cards);
    }

    @Test
    void shouldThrowExceptionWhenLessThanFiveCardsAreProvided() {

        List<Card> cards = List.of(
                createCard(Rank.ACE, Suit.SPADES),
                createCard(Rank.KING, Suit.HEARTS),
                createCard(Rank.QUEEN, Suit.DIAMONDS),
                createCard(Rank.JACK, Suit.CLUBS)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> handEvaluatorService.evaluate(cards)
        );
    }

    @Test
    void shouldThrowExceptionWhenCardsAreNull() {

        assertThrows(
                IllegalArgumentException.class,
                () -> handEvaluatorService.evaluate(null)
        );
    }

    private Card createCard(Rank rank, Suit suit) {
        return Card.builder()
                .rank(rank)
                .suit(suit)
                .build();
    }

    @Test
    void shouldEvaluateNextStrategyWhenPreviousDoesNotMatch() {

        List<Card> cards = List.of(
                createCard(Rank.ACE, Suit.SPADES),
                createCard(Rank.ACE, Suit.HEARTS),
                createCard(Rank.KING, Suit.DIAMONDS),
                createCard(Rank.QUEEN, Suit.CLUBS),
                createCard(Rank.JACK, Suit.SPADES)
        );
        HandEvaluation pairEvaluation = new HandEvaluation(
                HandRank.ONE_PAIR,
                List.of(14, 13, 12, 11),
                cards
        );
        when(royalFlushStrategy.getRank()).thenReturn(HandRank.ROYAL_FLUSH);
        when(onePairStrategy.getRank()).thenReturn(HandRank.ONE_PAIR);
        when(highCardStrategy.getRank()).thenReturn(HandRank.HIGH_CARD);
        when(royalFlushStrategy.evaluate(cards)).thenReturn(Optional.empty());
        when(onePairStrategy.evaluate(cards)).thenReturn(Optional.of(pairEvaluation));
        HandEvaluation result = handEvaluatorService.evaluate(cards);
        assertEquals(HandRank.ONE_PAIR, result.rank());
        verify(royalFlushStrategy).evaluate(cards);
        verify(onePairStrategy).evaluate(cards);
        verify(highCardStrategy, never()).evaluate(cards);

    }

    @Test
    void shouldReturnFirstValidEvaluation() {

        List<Card> cards = List.of(
                createCard(Rank.ACE, Suit.SPADES),
                createCard(Rank.ACE, Suit.HEARTS),
                createCard(Rank.KING, Suit.DIAMONDS),
                createCard(Rank.QUEEN, Suit.CLUBS),
                createCard(Rank.JACK, Suit.SPADES)
        );
        HandEvaluation pairEvaluation = new HandEvaluation(
                HandRank.ONE_PAIR,
                List.of(14, 13, 12, 11),
                cards
        );
        when(royalFlushStrategy.getRank()).thenReturn(HandRank.ROYAL_FLUSH);
        when(onePairStrategy.getRank()).thenReturn(HandRank.ONE_PAIR);
        when(highCardStrategy.getRank()).thenReturn(HandRank.HIGH_CARD);
        when(royalFlushStrategy.evaluate(cards)).thenReturn(Optional.empty());
        when(onePairStrategy.evaluate(cards)).thenReturn(Optional.of(pairEvaluation));
        HandEvaluation result = handEvaluatorService.evaluate(cards);
        assertEquals(HandRank.ONE_PAIR, result.rank());
        verify(royalFlushStrategy).evaluate(cards);
        verify(onePairStrategy).evaluate(cards);
        verify(highCardStrategy, never()).evaluate(cards);

    }

    @Test
    void shouldThrowExceptionWhenNoStrategyMatches() {

        List<Card> cards = List.of(
                createCard(Rank.ACE, Suit.SPADES),
                createCard(Rank.KING, Suit.HEARTS),
                createCard(Rank.QUEEN, Suit.DIAMONDS),
                createCard(Rank.JACK, Suit.CLUBS),
                createCard(Rank.NINE, Suit.SPADES)
        );
        when(royalFlushStrategy.getRank()).thenReturn(HandRank.ROYAL_FLUSH);
        when(onePairStrategy.getRank()).thenReturn(HandRank.ONE_PAIR);
        when(highCardStrategy.getRank()).thenReturn(HandRank.HIGH_CARD);
        when(royalFlushStrategy.evaluate(cards)).thenReturn(Optional.empty());
        when(onePairStrategy.evaluate(cards)).thenReturn(Optional.empty());
        when(highCardStrategy.evaluate(cards)).thenReturn(Optional.empty());
        assertThrows(
                IllegalStateException.class,
                () -> handEvaluatorService.evaluate(cards)
        );
        verify(royalFlushStrategy).evaluate(cards);
        verify(onePairStrategy).evaluate(cards);
        verify(highCardStrategy).evaluate(cards);

    }
}