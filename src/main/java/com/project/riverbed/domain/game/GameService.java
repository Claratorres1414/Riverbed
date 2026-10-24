
package com.project.riverbed.domain.game;

import com.project.riverbed.domain.game.dtos.AvailableGameResponse;
import com.project.riverbed.domain.game.dtos.StartGameRequest;
import com.project.riverbed.domain.game.dtos.StartGameResponse;
import com.project.riverbed.domain.game.exceptions.GameAlreadyStartedException;
import com.project.riverbed.domain.game.exceptions.GameNotFoundException;
import com.project.riverbed.domain.game.exceptions.InsufficientPlayersException;
import com.project.riverbed.domain.game.exceptions.UnauthorizedGameStartException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

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

    public StartGameResponse startGame(UUID idPartida, StartGameRequest request) {
        Game game = findGame(idPartida);

        validateStartGameRequest(game, request);

        game.setStatus(GameStatus.EM_ANDAMENTO);
        Game updatedGame = gameRepository.save(game);

        return new StartGameResponse(
                updatedGame.getId(),
                updatedGame.getStatus()
        );
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

    private Game findGame(UUID idPartida) {
        return gameRepository.findById(idPartida)
                .orElseThrow(GameNotFoundException::new);
    }

    private long countPlayers(UUID idPartida) {
        return gameRepository.countPlayersByGameId(idPartida);
    }
}
