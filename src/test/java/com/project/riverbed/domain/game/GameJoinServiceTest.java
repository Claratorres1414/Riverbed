package com.project.riverbed.domain.game;

import com.project.riverbed.domain.game.dtos.JoinGameResponse;
import com.project.riverbed.domain.game.exceptions.GameAlreadyStartedException;
import com.project.riverbed.domain.game.exceptions.GameFullException;
import com.project.riverbed.domain.game.exceptions.GameNotFoundException;
import com.project.riverbed.domain.game.exceptions.PlayerAlreadyInGameException;
import com.project.riverbed.domain.player.exception.PlayerNotFoundException;
import com.project.riverbed.domain.user.User;
import com.project.riverbed.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameJoinServiceTest {

    @Mock
    private GameRepository gameRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GamePlayerRepository gamePlayerRepository;
    @InjectMocks
    private GameService gameService;

    private Game game;
    private User player;
    private UUID gameId;
    private UUID playerId;

    @BeforeEach
    void setUp() {
        gameId = UUID.randomUUID();
        playerId = UUID.randomUUID();

        User creator = User.builder()
                .id(UUID.randomUUID())
                .name("Criador")
                .build();

        player = User.builder()
                .id(playerId)
                .name("Jogador")
                .build();

        game = Game.builder()
                .id(gameId)
                .creator(creator)
                .maxPlayers(6)
                .status(GameStatus.AGUARDANDO_JOGADORES)
                .build();
    }

    @Test
    @DisplayName("Deve adicionar o jogador quando a mesa está disponível")
    void shouldJoinAvailableGame() {
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(userRepository.findById(playerId)).thenReturn(Optional.of(player));
        when(gameRepository.countPlayersByGameId(gameId)).thenReturn(2L);
        when(gameRepository.existsByGameIdAndPlayerId(gameId, playerId)).thenReturn(false);

        JoinGameResponse response = gameService.joinGame(gameId, playerId);

        assertNotNull(response);
        assertEquals(gameId, response.gameId());
        assertEquals(playerId, response.playerId());

        ArgumentCaptor<GamePlayer> captor = ArgumentCaptor.forClass(GamePlayer.class);
        verify(gamePlayerRepository).save(captor.capture());
        assertEquals(game, captor.getValue().getGame());
        assertEquals(player, captor.getValue().getPlayer());
    }

    @Test
    @DisplayName("Deve permitir entrar quando resta exatamente uma vaga")
    void shouldJoinWhenOneSeatIsLeft() {
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(userRepository.findById(playerId)).thenReturn(Optional.of(player));
        when(gameRepository.countPlayersByGameId(gameId)).thenReturn(5L);
        when(gameRepository.existsByGameIdAndPlayerId(gameId, playerId)).thenReturn(false);

        assertDoesNotThrow(() -> gameService.joinGame(gameId, playerId));

        verify(gamePlayerRepository).save(any(GamePlayer.class));
    }

    @Test
    @DisplayName("Deve lançar GameNotFoundException quando a mesa não existir")
    void shouldRejectWhenGameDoesNotExist() {
        when(gameRepository.findById(gameId)).thenReturn(Optional.empty());

        assertThrows(GameNotFoundException.class,
                () -> gameService.joinGame(gameId, playerId));

        verify(userRepository, never()).findById(any());
        verify(gamePlayerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar PlayerNotFoundException quando o jogador não existir")
    void shouldRejectWhenPlayerDoesNotExist() {
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(userRepository.findById(playerId)).thenReturn(Optional.empty());

        assertThrows(PlayerNotFoundException.class,
                () -> gameService.joinGame(gameId, playerId));

        verify(gamePlayerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve rejeitar entrada em partida em andamento")
    void shouldRejectWhenGameIsInProgress() {
        game.setStatus(GameStatus.EM_ANDAMENTO);
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(userRepository.findById(playerId)).thenReturn(Optional.of(player));

        assertThrows(GameAlreadyStartedException.class,
                () -> gameService.joinGame(gameId, playerId));

        verify(gameRepository, never()).countPlayersByGameId(any());
        verify(gamePlayerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve rejeitar entrada em partida encerrada")
    void shouldRejectWhenGameIsClosed() {
        game.setStatus(GameStatus.ENCERRADA);
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(userRepository.findById(playerId)).thenReturn(Optional.of(player));

        assertThrows(GameAlreadyStartedException.class,
                () -> gameService.joinGame(gameId, playerId));

        verify(gameRepository, never()).countPlayersByGameId(any());
        verify(gamePlayerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar GameFullException quando a mesa está cheia")
    void shouldRejectWhenGameIsFull() {
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(userRepository.findById(playerId)).thenReturn(Optional.of(player));
        when(gameRepository.countPlayersByGameId(gameId)).thenReturn(6L);

        assertThrows(GameFullException.class,
                () -> gameService.joinGame(gameId, playerId));

        verify(gameRepository, never()).existsByGameIdAndPlayerId(any(), any());
        verify(gamePlayerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar PlayerAlreadyInGameException quando o jogador já está na mesa")
    void shouldRejectWhenPlayerAlreadyInGame() {
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(userRepository.findById(playerId)).thenReturn(Optional.of(player));
        when(gameRepository.countPlayersByGameId(gameId)).thenReturn(2L);
        when(gameRepository.existsByGameIdAndPlayerId(gameId, playerId)).thenReturn(true);

        assertThrows(PlayerAlreadyInGameException.class,
                () -> gameService.joinGame(gameId, playerId));

        verify(gamePlayerRepository, never()).save(any());
    }
}