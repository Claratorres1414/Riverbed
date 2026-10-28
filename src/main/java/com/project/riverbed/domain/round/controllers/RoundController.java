package com.project.riverbed.domain.round.controllers;

import com.project.riverbed.domain.round.dtos.PrivateCardsResponse;
import com.project.riverbed.domain.round.dtos.RoundStartResponse;
import com.project.riverbed.domain.round.services.RoundService;
import com.project.riverbed.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/poker/{gameId}/rounds")
@RequiredArgsConstructor
public class RoundController {

    private final RoundService roundService;

    @PostMapping("/start")
    public ResponseEntity<ApiResponse<RoundStartResponse>> startRound(
            @PathVariable UUID gameId
    ) {
        RoundStartResponse response = roundService.startRound(gameId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Rodada iniciada com sucesso!"
                )
        );
    }

    @GetMapping("/{roundId}/players/{playerId}/cards")
    public ResponseEntity<ApiResponse<PrivateCardsResponse>> getPrivateCards(
            @PathVariable UUID gameId,
            @PathVariable UUID roundId,
            @PathVariable UUID playerId
    ) {
        PrivateCardsResponse response =
                roundService.getPrivateCards(roundId, playerId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Cartas privadas consultadas com sucesso!")
        );
    }
}
