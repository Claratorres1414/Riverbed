package com.project.riverbed.domain.game;

import com.project.riverbed.domain.game.dtos.AvailableGameResponse;
import com.project.riverbed.domain.game.dtos.JoinGameResponse;
import com.project.riverbed.domain.game.dtos.StartGameRequest;
import com.project.riverbed.domain.game.dtos.StartGameResponse;
import com.project.riverbed.domain.game.exceptions.*;
import com.project.riverbed.domain.player.exception.PlayerNotFoundException;
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

        validatePlayerExists(playerId);
        validateJoinGame(game, playerId);

        return new JoinGameResponse(game.getId(), playerId);
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