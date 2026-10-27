package com.project.riverbed.domain.game;

import com.project.riverbed.domain.game.dtos.AvailableGameResponse;
import com.project.riverbed.domain.game.dtos.JoinGameResponse;
import com.project.riverbed.domain.game.dtos.StartGameRequest;
import com.project.riverbed.domain.game.dtos.StartGameResponse;
import com.project.riverbed.domain.game.exceptions.GameAlreadyStartedException;
import com.project.riverbed.domain.game.exceptions.GameNotFoundException;
import com.project.riverbed.domain.game.exceptions.InsufficientPlayersException;
import com.project.riverbed.domain.game.exceptions.UnauthorizedGameStartException;
import com.project.riverbed.domain.game.repositorys.GamePlayerRepository;
import com.project.riverbed.domain.game.repositorys.GameRepository;
import com.project.riverbed.domain.game.exceptions.*;
import com.project.riverbed.domain.player.exception.PlayerNotFoundException;
import com.project.riverbed.domain.user.User;
import com.project.riverbed.domain.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final GamePlayerRepository gamePlayerRepository;

    public List<AvailableGameResponse> findAvailableGames() {
        return gameRepository.findAvailableGames()
                .stream()
                .map(game -> new AvailableGameResponse(
                        game.getId(),
                        game.getCurrentPlayers(),
                        game.getMaxPlayers()
                ))
                .toList();
    }

    @Transactional
    public JoinGameResponse joinGame(UUID gameId, UUID playerId) {
        
        Game game = findGame(gameId);
        User player = userRepository.findById(playerId)
                .orElseThrow(PlayerNotFoundException::new);

        validateJoinGame(game, playerId);

        GamePlayerId gamePlayerId = new GamePlayerId(game.getId(), player.getId());

        GamePlayer gamePlayer = GamePlayer.builder()
                .id(gamePlayerId)
                .game(game)
                .player(player)
                .build();

        gamePlayerRepository.save(gamePlayer);

        return new JoinGameResponse(game.getId(), player.getId());
    }

    private void validateJoinGame(Game game, UUID playerId) {
        if (game.getStatus() != GameStatus.AGUARDANDO_JOGADORES) {
            throw new GameAlreadyStartedException();
        }

        if (countPlayers(game.getId()) >= game.getMaxPlayers()) {
            throw new GameFullException();
        }

        if (gameRepository.existsByGameIdAndPlayerId(game.getId(), playerId)) {
            throw new PlayerAlreadyInGameException();
        }
    }

    @Transactional
    public StartGameResponse startGame(UUID gameId, StartGameRequest request) {
        Game game = findGame(gameId);

        validateStartGameRequest(game, request);

        game.setStatus(GameStatus.EM_ANDAMENTO);
        Game updatedGame = gameRepository.save(game);

        return new StartGameResponse(
                updatedGame.getId(),
                updatedGame.getStatus()
        );
    }

    private void validatePlayerExists(UUID playerId) {
        if (!userRepository.existsById(playerId)) {
            throw new PlayerNotFoundException();
        }
    }

    private void validateStartGameRequest(Game game, StartGameRequest request) {
        if (!game.getCreator().getId().equals(request.playerId())) {
            throw new UnauthorizedGameStartException();
        }

        if (game.getStatus() != GameStatus.AGUARDANDO_JOGADORES) {
            throw new GameAlreadyStartedException();
        }

        if (countPlayers(game.getId()) < 2) {
            throw new InsufficientPlayersException();
        }
    }

    private Game findGame(UUID gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(GameNotFoundException::new);
    }

    private long countPlayers(UUID gameId) {
        return gameRepository.countPlayersByGameId(gameId);
    }
}