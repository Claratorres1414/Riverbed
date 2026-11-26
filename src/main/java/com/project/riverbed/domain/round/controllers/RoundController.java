package com.project.riverbed.domain.round.controllers;

import com.project.riverbed.domain.round.dtos.PrivateCardsResponse;
import com.project.riverbed.domain.round.dtos.RoundStartResponse;
import com.project.riverbed.domain.round.dtos.TableCardsResponse;
import com.project.riverbed.domain.round.facade.RoundFacade;
import com.project.riverbed.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/poker/rounds")
@RequiredArgsConstructor
public class RoundController {

    private final RoundFacade roundFacade;

    @PostMapping("/{gameId}/start")
    public ResponseEntity<ApiResponse<RoundStartResponse>> startRound(
            @PathVariable UUID gameId
    ) {
        RoundStartResponse response = roundFacade.startRound(gameId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Rodada iniciada com sucesso!"
                )
        );
    }

    @GetMapping("/{roundId}/players/{playerId}/cards")
    public ResponseEntity<ApiResponse<PrivateCardsResponse>> getPrivateCards(
            @PathVariable UUID roundId,
            @PathVariable UUID playerId
    ) {
        PrivateCardsResponse response =
                roundFacade.getPrivateCards(roundId, playerId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Cartas privadas consultadas com sucesso!")
        );
    }

    @PostMapping("/{roundId}/distribute/community-cards")
    public ResponseEntity<ApiResponse<TableCardsResponse>> tempDistributeAllCommunityCards(
            @PathVariable UUID roundId
    ) {
        TableCardsResponse response = roundFacade.tempDistributeTableCards(roundId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Cartas da mesa distribuídas com sucesso!")
        );
    }

    @GetMapping("/{roundId}/check/community-cards")
    public ResponseEntity<ApiResponse<TableCardsResponse>> checkAllCommunityCards(
            @PathVariable UUID roundId
    ) {
        TableCardsResponse response = roundFacade.getAllTableCards(roundId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Cartas da mesa buscadas com sucesso!")
        );
    }

    @PostMapping("/{roundId}/end")
    public ResponseEntity<ApiResponse<Void>> endRound(
            @PathVariable UUID roundId
    ) {
        roundFacade.endRound(roundId);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Rodada encerrada com sucesso!")
        );
    }
}
