package com.project.riverbed.domain.round;

import com.project.riverbed.domain.card.entitys.Card;
import com.project.riverbed.domain.deck.services.CardDistributionService;
import com.project.riverbed.domain.game.Game;
import com.project.riverbed.domain.game.repositorys.GamePlayerRepository;
import com.project.riverbed.domain.game.repositorys.GameRepository;
import com.project.riverbed.domain.game.GameStatus;
import com.project.riverbed.domain.game.exceptions.GameNotFoundException;
import com.project.riverbed.domain.game.exceptions.InsufficientPlayersException;
import com.project.riverbed.domain.round.dtos.RoundStartResponse;
import com.project.riverbed.domain.round.entitys.Round;
import com.project.riverbed.domain.round.enums.RoundStatus;
import com.project.riverbed.domain.round.repositorys.RoundRepository;
import com.project.riverbed.domain.round.services.RoundService;
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
}