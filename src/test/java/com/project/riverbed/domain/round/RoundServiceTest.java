package com.project.riverbed.domain.round;

import com.project.riverbed.domain.card.entitys.Card;
import com.project.riverbed.domain.card.enums.Rank;
import com.project.riverbed.domain.card.enums.Suit;
import com.project.riverbed.domain.deck.services.CardDistributionService;
import com.project.riverbed.domain.game.Game;
import com.project.riverbed.domain.game.repositorys.GamePlayerRepository;
import com.project.riverbed.domain.game.repositorys.GameRepository;
import com.project.riverbed.domain.game.GameStatus;
import com.project.riverbed.domain.game.exceptions.GameNotFoundException;
import com.project.riverbed.domain.game.exceptions.InsufficientPlayersException;
import com.project.riverbed.domain.round.dtos.PrivateCardsResponse;
import com.project.riverbed.domain.round.dtos.RoundStartResponse;
import com.project.riverbed.domain.round.entitys.Round;
import com.project.riverbed.domain.round.entitys.RoundPlayerCard;
import com.project.riverbed.domain.round.enums.RoundStatus;
import com.project.riverbed.domain.round.exceptions.PlayerNotInRoundException;
import com.project.riverbed.domain.round.exceptions.RoundNotFoundException;
import com.project.riverbed.domain.round.repositorys.RoundPlayerCardRepository;
import com.project.riverbed.domain.round.repositorys.RoundRepository;
import com.project.riverbed.domain.round.services.RoundService;
import com.project.riverbed.domain.user.User;
import com.project.riverbed.domain.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoundServiceTest {

    @Mock
    private RoundPlayerCardRepository roundPlayerCardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoundRepository roundRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GamePlayerRepository gamePlayerRepository;

    @Mock
    private CardDistributionService cardDistributionService;

    @InjectMocks
    private RoundService roundService;

    @Test
    void shouldThrowWhenGameDoesNotExist() {
        UUID gameId = UUID.randomUUID();

        when(gameRepository.findById(gameId))
                .thenReturn(Optional.empty());

        assertThrows(
                GameNotFoundException.class,
                () -> roundService.startRound(gameId)
        );

        verifyNoInteractions(
                roundRepository,
                gamePlayerRepository,
                cardDistributionService
        );
    }

    @Test
    void shouldThrowWhenGameIsNotInProgress() {
        UUID gameId = UUID.randomUUID();

        Game game = Game.builder()
                .id(gameId)
                .status(GameStatus.AGUARDANDO_JOGADORES)
                .build();

        when(gameRepository.findById(gameId))
                .thenReturn(Optional.of(game));

        assertThrows(
                IllegalStateException.class,
                () -> roundService.startRound(gameId)
        );

        verifyNoInteractions(
                gamePlayerRepository,
                cardDistributionService
        );
    }

    @Test
    void shouldThrowWhenRoundIsAlreadyInProgress() {
        UUID gameId = UUID.randomUUID();

        Game game = Game.builder()
                .id(gameId)
                .status(GameStatus.EM_ANDAMENTO)
                .build();

        when(gameRepository.findById(gameId))
                .thenReturn(Optional.of(game));

        when(roundRepository.existsByGameIdAndStatus(
                gameId, RoundStatus.EM_ANDAMENTO
        )).thenReturn(true);

        assertThrows(
                IllegalStateException.class,
                () -> roundService.startRound(gameId)
        );

        verifyNoInteractions(
                gamePlayerRepository,
                cardDistributionService
        );
    }

    @Test
    void shouldThrowWhenThereAreNotEnoughPlayers() {
        UUID gameId = UUID.randomUUID();

        Game game = Game.builder()
                .id(gameId)
                .status(GameStatus.EM_ANDAMENTO)
                .build();

        when(gameRepository.findById(gameId))
                .thenReturn(Optional.of(game));

        when(roundRepository.existsByGameIdAndStatus(
                gameId, RoundStatus.EM_ANDAMENTO
        )).thenReturn(false);

        when(gamePlayerRepository.findPlayerIdsByGameId(gameId))
                .thenReturn(List.of(UUID.randomUUID()));

        assertThrows(
                InsufficientPlayersException.class,
                () -> roundService.startRound(gameId)
        );

        verify(roundRepository, never()).save(any(Round.class));
        verifyNoInteractions(cardDistributionService);
    }

    @Test
    void shouldStartRoundAndDistributeCards() {
        UUID gameId = UUID.randomUUID();
        UUID player1 = UUID.randomUUID();
        UUID player2 = UUID.randomUUID();
        UUID roundId = UUID.randomUUID();

        Game game = Game.builder()
                .id(gameId)
                .status(GameStatus.EM_ANDAMENTO)
                .build();

        List<UUID> playerIds = List.of(player1, player2);

        Map<UUID, List<Card>> distribution = Map.of(
                player1, List.of(mock(Card.class), mock(Card.class)),
                player2, List.of(mock(Card.class), mock(Card.class))
        );

        when(gameRepository.findById(gameId))
                .thenReturn(Optional.of(game));

        when(roundRepository.existsByGameIdAndStatus(
                gameId, RoundStatus.EM_ANDAMENTO
        )).thenReturn(false);

        when(gamePlayerRepository.findPlayerIdsByGameId(gameId))
                .thenReturn(playerIds);

        when(cardDistributionService.distribute(playerIds))
                .thenReturn(distribution);

        when(roundRepository.save(any(Round.class)))
                .thenAnswer(invocation -> {
                    Round round = invocation.getArgument(0);

                    if (round.getId() == null) {
                        round.setId(roundId);
                    }

                    return round;
                });

        User user1 = User.builder()
                .id(player1)
                .build();

        User user2 = User.builder()
                .id(player2)
                .build();

        when(userRepository.findById(player1))
                .thenReturn(Optional.of(user1));

        when(userRepository.findById(player2))
                .thenReturn(Optional.of(user2));

        RoundStartResponse response = roundService.startRound(gameId);

        assertNotNull(response);
        assertEquals(roundId, response.roundId());
        assertEquals(RoundStatus.EM_ANDAMENTO, response.status());
        assertEquals(distribution, response.distribution());

        verify(cardDistributionService).distribute(playerIds);

        verify(roundRepository, times(2)).save(argThat(
                Round::isCardsDistributed
        ));

        verify(roundRepository, times(2)).save(any(Round.class));
    }

    @Test
    void shouldReturnPrivateCardsForPlayer() {
        UUID roundId = UUID.randomUUID();
        UUID gameId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();

        Card card1 = Card.builder()
                .id(1L)
                .suit(Suit.HEARTS)
                .rank(Rank.ACE)
                .build();

        Card card2 = Card.builder()
                .id(2L)
                .suit(Suit.SPADES)
                .rank(Rank.KING)
                .build();

        Game game = Game.builder()
                .id(gameId)
                .build();

        Round round = Round.builder()
                .id(roundId)
                .game(game)
                .build();

        User player = User.builder()
                .id(playerId)
                .build();

        RoundPlayerCard playerCard1 = RoundPlayerCard.builder()
                .round(round)
                .player(player)
                .card(card1)
                .position((short) 1)
                .build();

        RoundPlayerCard playerCard2 = RoundPlayerCard.builder()
                .round(round)
                .player(player)
                .card(card2)
                .position((short) 2)
                .build();

        when(roundRepository.findById(roundId))
                .thenReturn(Optional.of(round));

        when(gamePlayerRepository.existsByGameIdAndPlayerId(gameId, playerId))
                .thenReturn(true);

        when(roundPlayerCardRepository
                .findByRoundIdAndPlayerIdOrderByPosition(roundId, playerId))
                .thenReturn(List.of(playerCard1, playerCard2));

        PrivateCardsResponse response =
                roundService.getPrivateCards(roundId, playerId);

        assertNotNull(response);
        assertEquals(2, response.cards().size());

        assertEquals(1L, response.cards().get(0).id());
        assertEquals(Suit.HEARTS, response.cards().get(0).suit());
        assertEquals(Rank.ACE, response.cards().get(0).rank());

        assertEquals(2L, response.cards().get(1).id());
        assertEquals(Suit.SPADES, response.cards().get(1).suit());
        assertEquals(Rank.KING, response.cards().get(1).rank());

        verify(roundPlayerCardRepository)
                .findByRoundIdAndPlayerIdOrderByPosition(roundId, playerId);
    }

    @Test
    void shouldReturnEmptyListWhenPlayerHasNoPrivateCards() {
        UUID roundId = UUID.randomUUID();
        UUID gameId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();

        Game game = Game.builder()
                .id(gameId)
                .build();

        Round round = Round.builder()
                .id(roundId)
                .game(game)
                .build();

        when(roundRepository.findById(roundId))
                .thenReturn(Optional.of(round));

        when(gamePlayerRepository.existsByGameIdAndPlayerId(gameId, playerId))
                .thenReturn(true);

        when(roundPlayerCardRepository
                .findByRoundIdAndPlayerIdOrderByPosition(roundId, playerId))
                .thenReturn(List.of());

        PrivateCardsResponse response =
                roundService.getPrivateCards(roundId, playerId);

        assertNotNull(response);
        assertTrue(response.cards().isEmpty());
    }

    @Test
    void shouldReturnOnlyCardsBelongingToRequestedPlayer() {
        UUID roundId = UUID.randomUUID();
        UUID gameId = UUID.randomUUID();
        UUID player1Id = UUID.randomUUID();
        UUID player2Id = UUID.randomUUID();

        Game game = Game.builder()
                .id(gameId)
                .build();

        Round round = Round.builder()
                .id(roundId)
                .game(game)
                .build();

        User player1 = User.builder()
                .id(player1Id)
                .build();

        Card card = Card.builder()
                .id(1L)
                .suit(Suit.HEARTS)
                .rank(Rank.ACE)
                .build();

        RoundPlayerCard playerCard = RoundPlayerCard.builder()
                .round(round)
                .player(player1)
                .card(card)
                .position((short) 1)
                .build();

        when(roundRepository.findById(roundId))
                .thenReturn(Optional.of(round));

        when(gamePlayerRepository.existsByGameIdAndPlayerId(gameId, player1Id))
                .thenReturn(true);

        when(roundPlayerCardRepository
                .findByRoundIdAndPlayerIdOrderByPosition(roundId, player1Id))
                .thenReturn(List.of(playerCard));

        PrivateCardsResponse response =
                roundService.getPrivateCards(roundId, player1Id);

        assertNotNull(response);
        assertEquals(1, response.cards().size());
        assertEquals(1L, response.cards().get(0).id());

        verify(roundPlayerCardRepository)
                .findByRoundIdAndPlayerIdOrderByPosition(roundId, player1Id);

        verify(roundPlayerCardRepository, never())
                .findByRoundIdAndPlayerIdOrderByPosition(roundId, player2Id);
    }

    @Test
    void shouldThrowWhenRoundDoesNotExist() {
        UUID roundId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();

        when(roundRepository.findById(roundId))
                .thenReturn(Optional.empty());

        assertThrows(
                RoundNotFoundException.class,
                () -> roundService.getPrivateCards(roundId, playerId)
        );

        verifyNoInteractions(gamePlayerRepository, roundPlayerCardRepository);
    }

    @Test
    void shouldThrowWhenPlayerDoesNotParticipateInRound() {
        UUID roundId = UUID.randomUUID();
        UUID gameId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();

        Game game = Game.builder()
                .id(gameId)
                .build();

        Round round = Round.builder()
                .id(roundId)
                .game(game)
                .build();

        when(roundRepository.findById(roundId))
                .thenReturn(Optional.of(round));

        when(gamePlayerRepository.existsByGameIdAndPlayerId(gameId, playerId))
                .thenReturn(false);

        assertThrows(
                PlayerNotInRoundException.class,
                () -> roundService.getPrivateCards(roundId, playerId)
        );

        verify(roundPlayerCardRepository, never())
                .findByRoundIdAndPlayerIdOrderByPosition(roundId, playerId);
    }
}