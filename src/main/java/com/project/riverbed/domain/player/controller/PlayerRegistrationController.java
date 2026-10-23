package com.project.riverbed.domain.player.controller;

import com.project.riverbed.domain.player.dto.PlayerRegistrationRequest;
import com.project.riverbed.domain.player.dto.PlayerRegistrationResponse;
import com.project.riverbed.domain.player.service.PlayerRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador responsável pelo endpoint de cadastro de novos jogadores (RF06).
 */
@RestController
@RequestMapping("/poker")
@RequiredArgsConstructor
public class PlayerRegistrationController {

    private final PlayerRegistrationService playerRegistrationService;

    /**
     * Realiza o cadastro de uma nova conta de jogador (RF01, RF06).
     * Retorna o identificador único (id) do jogador criado com status 201 Created (RF07, CA04).
     */
    @PostMapping("/cadastro")
    public ResponseEntity<PlayerRegistrationResponse> registerPlayer(
            @Valid @RequestBody PlayerRegistrationRequest request
    ) {
        PlayerRegistrationResponse response = playerRegistrationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
