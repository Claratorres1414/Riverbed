package com.project.riverbed.domain.deck.entitys;

import com.project.riverbed.domain.card.entitys.Card;
import com.project.riverbed.domain.card.enums.Rank;
import com.project.riverbed.domain.card.enums.Suit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

class DeckTest {

    private List<Card> cards;
    @BeforeEach
    void setUp() {
        cards = createStandardDeck();
    }
    @Test
    void shouldCreateDeckWith52Cards() {
        Deck deck = new Deck(cards);
        assertThat(deck.remainingCards()).isEqualTo(52);
        assertThat(deck.getCards()).hasSize(52);
    }
    @Test
    void shouldRejectDeckWithNullCards() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Deck(null))
                .withMessage("O baralho deve ter exatamente 52 cartas.");
    }
    @Test
    void shouldRejectDeckWithFewerThan52Cards() {
        List<Card> incompleteDeck = new ArrayList<>(cards);
        incompleteDeck.removeLast();
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Deck(incompleteDeck))
                .withMessage("O baralho deve ter exatamente 52 cartas.");
    }
    @Test
    void shouldRejectDeckWithDuplicateCards() {
        List<Card> duplicatedDeck = new ArrayList<>(cards);
        duplicatedDeck.set(duplicatedDeck.size() - 1, duplicatedDeck.getFirst());
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Deck(duplicatedDeck))
                .withMessage("O baralho não pode conter cartas duplicadas.");
    }
    @Test
    void shouldRejectDeckContainingNullCard() {
        List<Card> invalidDeck = new ArrayList<>(cards);
        invalidDeck.set(0, null);
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Deck(invalidDeck))
                .withMessage("O baralho não pode conter cartas inválidas.");
    }
    @Test
    void shouldRejectCardWithoutSuit() {
        List<Card> invalidDeck = new ArrayList<>(cards);
        invalidDeck.set(
                0,
                Card.builder()
                        .rank(cards.getFirst().getRank())
                        .build()
        );
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Deck(invalidDeck))
                .withMessage("O baralho não pode conter cartas inválidas.");
    }
    @Test
    void shouldRejectCardWithoutRank() {
        List<Card> invalidDeck = new ArrayList<>(cards);
        invalidDeck.set(
                0,
                Card.builder()
                        .suit(cards.getFirst().getSuit())
                        .build()
        );
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Deck(invalidDeck))
                .withMessage("O baralho não pode conter cartas inválidas.");
    }
    @Test
    void shouldDrawOneCard() {
        Deck deck = new Deck(cards);
        Card drawnCard = deck.draw();
        assertThat(drawnCard).isNotNull();
        assertThat(deck.remainingCards()).isEqualTo(51);
        assertThat(deck.getCards()).doesNotContain(drawnCard);
    }
    @Test
    void shouldDrawRequestedNumberOfCards() {
        Deck deck = new Deck(cards);
        List<Card> drawnCards = deck.draw(5);
        assertThat(drawnCards).hasSize(5);
        assertThat(deck.remainingCards()).isEqualTo(47);
        assertThat(new HashSet<>(drawnCards)).hasSize(5);
    }
    @Test
    void shouldNotAllowDrawingTheSameCardTwice() {
        Deck deck = new Deck(cards);
        List<Card> drawnCards = deck.draw(10);
        List<Card> remainingCards = deck.getCards();
        assertThat(drawnCards)
                .doesNotContainAnyElementsOf(remainingCards);
        assertThat(
                java.util.stream.Stream.concat(
                                drawnCards.stream(),
                                remainingCards.stream()
                        ).map(card -> card.getSuit().name() + "_" + card.getRank().name())
                        .distinct()
                        .count()
        ).isEqualTo(52);
    }
    @Test
    void shouldRejectDrawingZeroCards() {
        Deck deck = new Deck(cards);
        assertThatIllegalArgumentException()
                .isThrownBy(() -> deck.draw(0))
                .withMessage("A quantidade de cartas deve ser maior que zero.");
        assertThat(deck.remainingCards()).isEqualTo(52);
    }
    @Test
    void shouldRejectDrawingNegativeQuantity() {
        Deck deck = new Deck(cards);
        assertThatIllegalArgumentException()
                .isThrownBy(() -> deck.draw(-1))
                .withMessage("A quantidade de cartas deve ser maior que zero.");
        assertThat(deck.remainingCards()).isEqualTo(52);
    }
    @Test
    void shouldRejectDrawingMoreCardsThanAvailable() {
        Deck deck = new Deck(cards);
        assertThatIllegalStateException()
                .isThrownBy(() -> deck.draw(53))
                .withMessage("Não há cartas suficientes no baralho.");
        assertThat(deck.remainingCards()).isEqualTo(52);
    }
    @Test
    void shouldRejectDrawingFromEmptyDeck() {
        Deck deck = new Deck(cards);
        deck.draw(52);
        assertThat(deck.remainingCards()).isZero();
        assertThatIllegalStateException()
                .isThrownBy(deck::draw)
                .withMessage("Não há cartas disponíveis no baralho.");
    }
    @Test
    void shouldRejectDrawingMultipleCardsFromEmptyDeck() {
        Deck deck = new Deck(cards);
        deck.draw(52);
        assertThatIllegalStateException()
                .isThrownBy(() -> deck.draw(1))
                .withMessage("Não há cartas suficientes no baralho.");
    }
    @Test
    void shouldReturnImmutableListOfAvailableCards() {
        Deck deck = new Deck(cards);
        assertThat(
                org.assertj.core.api.Assertions.catchThrowable(
                        () -> deck.getCards().clear()
                )
        ).isInstanceOf(UnsupportedOperationException.class);
        assertThat(deck.remainingCards()).isEqualTo(52);
    }
    private List<Card> createStandardDeck() {
        List<Card> standardDeck = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                standardDeck.add(
                        Card.builder()
                                .suit(suit)
                                .rank(rank)
                                .build()
                );
            }
        }
        return standardDeck;
    }

}