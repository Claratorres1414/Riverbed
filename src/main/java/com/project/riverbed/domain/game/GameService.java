
package com.project.riverbed.domain.game;

import com.project.riverbed.domain.game.dtos.AvailableGameResponse;
import com.project.riverbed.domain.game.dtos.StartGameRequest;
import com.project.riverbed.domain.game.dtos.StartGameResponse;
import com.project.riverbed.domain.game.exceptions.GameNotFoundException;
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
        Game game = gameRepository.findById(idPartida)
                .orElseThrow(GameNotFoundException::new);

        return new StartGameResponse(
                game.getId(),
                game.getStatus()
        );
    }
}
