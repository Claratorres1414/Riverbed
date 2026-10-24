package com.project.riverbed.domain.deck.services;

import com.project.riverbed.domain.card.entitys.Card;
import com.project.riverbed.domain.card.enums.Rank;
import com.project.riverbed.domain.card.enums.Suit;
import com.project.riverbed.domain.card.repositorys.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardDistributionServiceTest {

    @Mock
    private CardRepository cardRepository;
    @InjectMocks
    private CardDistributionService cardDistributionService;
    private List<Card> cards;
    @BeforeEach
    void setUp() {
        cards = createStandardDeck();
        when(cardRepository.findAll()).thenReturn(cards);
    }
    @Test
    void shouldDistributeTwoCardsToEachPlayer() {
        List<UUID> playerIds = List.of(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
        Map<UUID, List<Card>> distribution =
                cardDistributionService.distribute(playerIds);
        assertThat(distribution).hasSize(3);
        for (UUID playerId : playerIds) {
            assertThat(distribution.get(playerId)).hasSize(2);
        }
    }
    @Test
    void shouldNotDistributeTheSameCardToDifferentPlayers() {
        List<UUID> playerIds = List.of(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
        Map<UUID, List<Card>> distribution =
                cardDistributionService.distribute(playerIds);
        List<Card> distributedCards = distribution.values()
                .stream()
                .flatMap(List::stream)
                .toList();
        Set<String> uniqueCards = new HashSet<>();
        for (Card card : distributedCards) {
            uniqueCards.add(card.getSuit().name() + "_" + card.getRank().name());
        }
        assertThat(distributedCards).hasSize(8);
        assertThat(uniqueCards).hasSize(8);
    }
    @Test
    void shouldDistributeCardsOnlyToReceivedPlayerIds() {
        List<UUID> playerIds = List.of(
                UUID.randomUUID(),
                UUID.randomUUID()
        );
        Map<UUID, List<Card>> distribution =
                cardDistributionService.distribute(playerIds);
        assertThat(distribution.keySet()).containsExactlyInAnyOrderElementsOf(playerIds);
    }
    @Test
    void shouldRetrieveCardsFromRepository() {
        List<UUID> playerIds = List.of(UUID.randomUUID());
        cardDistributionService.distribute(playerIds);
        verify(cardRepository).findAll();
    }
    @Test
    void shouldReturnImmutableDistributionMap() {
        List<UUID> playerIds = List.of(UUID.randomUUID());
        Map<UUID, List<Card>> distribution =
                cardDistributionService.distribute(playerIds);
        assertThatThrownBy(() ->
                distribution.put(UUID.randomUUID(), List.of())
        ).isInstanceOf(UnsupportedOperationException.class);
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