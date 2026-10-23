
package com.project.riverbed.domain.game;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
