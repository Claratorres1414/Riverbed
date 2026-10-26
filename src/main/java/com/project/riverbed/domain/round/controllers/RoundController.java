package com.project.riverbed.domain.round.controllers;

import com.project.riverbed.domain.round.dtos.RoundStartResponse;
import com.project.riverbed.domain.round.services.RoundService;
import com.project.riverbed.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
