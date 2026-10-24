package com.project.riverbed.domain.game;

import com.project.riverbed.domain.game.dtos.AvailableGameResponse;
import com.project.riverbed.domain.game.dtos.JoinGameRequest;
import com.project.riverbed.domain.game.dtos.JoinGameResponse;
import com.project.riverbed.domain.game.dtos.StartGameRequest;
import com.project.riverbed.domain.game.dtos.StartGameResponse;
import com.project.riverbed.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/poker")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @GetMapping("/open-games")
    public List<AvailableGameResponse> findAvailableGames() {
        return gameService.findAvailableGames();
    }

    @PostMapping("/{gameId}/join")
    public ResponseEntity<ApiResponse<JoinGameResponse>> joinGame(
            @PathVariable UUID gameId,
            @RequestBody JoinGameRequest request
    ) {
        JoinGameResponse response = gameService.joinGame(gameId, request.playerId());

        return ResponseEntity.ok(
                ApiResponse.success(response, "Jogador adicionado à partida com sucesso!")
        );
    }

    @PostMapping("/{gameId}/start")
    public ResponseEntity<ApiResponse<StartGameResponse>> startGame(
            @PathVariable UUID gameId,
            @RequestBody StartGameRequest request
    ) {
        StartGameResponse response = gameService.startGame(
                gameId,
                request
        );

        return ResponseEntity.ok(
                ApiResponse.success(response, "Partida iniciada com sucesso!")
        );
    }
}