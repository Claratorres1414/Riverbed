package com.project.riverbed.domain.game;

import com.project.riverbed.domain.game.dtos.StartGameRequest;
import com.project.riverbed.domain.game.dtos.StartGameResponse;
import com.project.riverbed.domain.game.exceptions.GameAlreadyStartedException;
import com.project.riverbed.domain.game.exceptions.GameNotFoundException;
import com.project.riverbed.domain.game.exceptions.InsufficientPlayersException;
import com.project.riverbed.domain.game.exceptions.UnauthorizedGameStartException;
import com.project.riverbed.domain.game.repositorys.GameRepository;
import com.project.riverbed.domain.user.User;
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
class GameServiceTest {
    @Mock
    private GameRepository gameRepository;
    @InjectMocks
    private GameService gameService;
    private Game game;
    private User creator;
    private UUID gameId;
    private UUID creatorId;
    private UUID otherPlayerId;
    @BeforeEach
    void setUp() {
        gameId = UUID.randomUUID();
        creatorId = UUID.randomUUID();
        otherPlayerId = UUID.randomUUID();
        creator = User.builder()
                .id(creatorId)
                .name("Criador")
                .build();
        game = Game.builder()
                .id(gameId)
                .creator(creator)
                .maxPlayers(6)
                .status(GameStatus.AGUARDANDO_JOGADORES)
                .build();
    }
    @Test
    @DisplayName("Deve lançar GameNotFoundException quando a partida não existir")
    void shouldThrowExceptionWhenGameDoesNotExist() {
        when(gameRepository.findById(gameId)).thenReturn(Optional.empty());
        assertThrows(
                GameNotFoundException.class,
                () -> gameService.startGame(gameId, new StartGameRequest(creatorId))
        );
        verify(gameRepository, never()).countPlayersByGameId(any());
        verify(gameRepository, never()).save(any(Game.class));
    }
    @Test
    @DisplayName("Deve rejeitar solicitação de jogador que não é o criador")
    void shouldRejectWhenRequesterIsNotCreator() {
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        assertThrows(
                UnauthorizedGameStartException.class,
                () -> gameService.startGame(gameId, new StartGameRequest(otherPlayerId))
        );
        verify(gameRepository, never()).countPlayersByGameId(any());
        verify(gameRepository, never()).save(any(Game.class));
        assertEquals(GameStatus.AGUARDANDO_JOGADORES, game.getStatus());
    }
    @Test
    @DisplayName("Deve rejeitar partida com menos de dois jogadores")
    void shouldRejectWhenThereAreInsufficientPlayers() {
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(gameRepository.countPlayersByGameId(gameId)).thenReturn(1L);
        assertThrows(
                InsufficientPlayersException.class,
                () -> gameService.startGame(gameId, new StartGameRequest(creatorId))
        );
        verify(gameRepository, never()).save(any(Game.class));
        assertEquals(GameStatus.AGUARDANDO_JOGADORES, game.getStatus());
    }
    @Test
    @DisplayName("Deve rejeitar partida que já está em andamento")
    void shouldRejectWhenGameIsAlreadyInProgress() {
        game.setStatus(GameStatus.EM_ANDAMENTO);
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        assertThrows(
                GameAlreadyStartedException.class,
                () -> gameService.startGame(gameId, new StartGameRequest(creatorId))
        );
        verify(gameRepository, never()).countPlayersByGameId(any());
        verify(gameRepository, never()).save(any(Game.class));
    }
    @Test
    @DisplayName("Deve rejeitar partida encerrada")
    void shouldRejectWhenGameIsClosed() {
        game.setStatus(GameStatus.ENCERRADA);
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        assertThrows(
                GameAlreadyStartedException.class,
                () -> gameService.startGame(gameId, new StartGameRequest(creatorId))
        );
        verify(gameRepository, never()).countPlayersByGameId(any());
        verify(gameRepository, never()).save(any(Game.class));
    }
    @Test
    @DisplayName("Deve iniciar partida quando o criador solicitar e houver jogadores suficientes")
    void shouldStartGameWhenRequestIsValid() {
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(gameRepository.countPlayersByGameId(gameId)).thenReturn(2L);
        when(gameRepository.save(any(Game.class))).thenAnswer(invocation ->
                invocation.getArgument(0)
        );
        StartGameResponse response = gameService.startGame(
                gameId,
                new StartGameRequest(creatorId)
        );
        assertNotNull(response);
        assertEquals(gameId, response.gameId());
        assertEquals(GameStatus.EM_ANDAMENTO, response.status());
        assertEquals(GameStatus.EM_ANDAMENTO, game.getStatus());
        verify(gameRepository).countPlayersByGameId(gameId);
        verify(gameRepository).save(game);
    }
    @Test
    @DisplayName("Deve persistir a partida com o estado EM_ANDAMENTO")
    void shouldPersistGameWithInProgressStatus() {
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(gameRepository.countPlayersByGameId(gameId)).thenReturn(3L);
        when(gameRepository.save(any(Game.class))).thenAnswer(invocation ->
                invocation.getArgument(0)
        );
        gameService.startGame(gameId, new StartGameRequest(creatorId));
        ArgumentCaptor<Game> captor = ArgumentCaptor.forClass(Game.class);
        verify(gameRepository).save(captor.capture());
        assertEquals(gameId, captor.getValue().getId());
        assertEquals(GameStatus.EM_ANDAMENTO, captor.getValue().getStatus());
    }
}