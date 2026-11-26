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
public class ThreeOfAKindStrategyTest {
    private ThreeOfAKindStrategy threeOfAKindStrategy;

    @BeforeEach
    void setUp() {
        threeOfAKindStrategy = new ThreeOfAKindStrategy();
    }

    @Test
    void shouldRecognizeThreeOfAKind() {

        List<Card> cards = List.of(
                createCard(Rank.ACE, Suit.SPADES),
                createCard(Rank.ACE, Suit.HEARTS),
                createCard(Rank.ACE, Suit.DIAMONDS),
                createCard(Rank.KING, Suit.CLUBS),
                createCard(Rank.QUEEN, Suit.SPADES)
        );

        Optional<HandEvaluation> result = threeOfAKindStrategy.evaluate(cards);

        assertTrue(result.isPresent());
        assertEquals(HandRank.THREE_OF_A_KIND, result.get().rank());
        assertEquals(List.of(14, 13, 12), result.get().tieBreakValues());
        assertEquals(5, result.get().bestFiveCards().size());
    }

    @Test
    void shouldSelectHighestKickers() {

        List<Card> cards = List.of(
                createCard(Rank.ACE, Suit.SPADES),
                createCard(Rank.ACE, Suit.HEARTS),
                createCard(Rank.ACE, Suit.DIAMONDS),
                createCard(Rank.KING, Suit.CLUBS),
                createCard(Rank.QUEEN, Suit.SPADES),
                createCard(Rank.JACK, Suit.HEARTS),
                createCard(Rank.TEN, Suit.DIAMONDS)
        );

        Optional<HandEvaluation> result = threeOfAKindStrategy.evaluate(cards);

        assertTrue(result.isPresent());
        assertEquals(List.of(14, 13, 12), result.get().tieBreakValues());

        assertTrue(result.get().bestFiveCards().stream()
                .anyMatch(card ->
                        card.getRank() == Rank.KING &&
                                card.getSuit() == Suit.CLUBS
                ));

        assertTrue(result.get().bestFiveCards().stream()
                .anyMatch(card ->
                        card.getRank() == Rank.QUEEN &&
                                card.getSuit() == Suit.SPADES
                ));

        assertFalse(result.get().bestFiveCards().stream()
                .anyMatch(card ->
                        card.getRank() == Rank.JACK ||
                                card.getRank() == Rank.TEN
                ));
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

        Optional<HandEvaluation> result = threeOfAKindStrategy.evaluate(cards);

        assertTrue(result.isEmpty());
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

        Optional<HandEvaluation> result = threeOfAKindStrategy.evaluate(cards);

        assertTrue(result.isEmpty());
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

        Optional<HandEvaluation> result = threeOfAKindStrategy.evaluate(cards);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenLessThanFiveCardsAreProvided() {

        List<Card> cards = List.of(
                createCard(Rank.ACE, Suit.SPADES),
                createCard(Rank.ACE, Suit.HEARTS),
                createCard(Rank.ACE, Suit.DIAMONDS),
                createCard(Rank.KING, Suit.CLUBS)
        );

        Optional<HandEvaluation> result = threeOfAKindStrategy.evaluate(cards);

        assertTrue(result.isEmpty());
    }

    private Card createCard(Rank rank, Suit suit) {
        return Card.builder()
                .rank(rank)
                .suit(suit)
                .build();
    }
}
