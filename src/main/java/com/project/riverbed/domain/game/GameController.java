
package com.project.riverbed.domain.game;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/poker")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @GetMapping("/open-games")
    public List<AvailableGameResponse> findAvailableGames() {
        return gameService.findAvailableGames();
    }
}
