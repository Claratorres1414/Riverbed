package com.project.riverbed.domain.game;

import com.project.riverbed.domain.game.dtos.JoinGameRequest;
import com.project.riverbed.domain.game.dtos.JoinGameResponse;
import com.project.riverbed.domain.game.exceptions.GameAlreadyStartedException;
import com.project.riverbed.domain.game.exceptions.GameFullException;
import com.project.riverbed.domain.game.exceptions.GameNotFoundException;
import com.project.riverbed.domain.game.exceptions.PlayerAlreadyInGameException;
import com.project.riverbed.domain.player.exception.PlayerNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // Boot 3.4+; antes: @MockBean
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Se houver Spring Security, adicione @AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(GameController.class)
class GameJoinControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private GameService gameService;

    private final UUID gameId = UUID.randomUUID();
    private final UUID playerId = UUID.randomUUID();

    private String body() {
        return "{\"playerId\":\"" + playerId + "\"}";
    }
    private org.springframework.test.web.servlet.ResultActions join() throws Exception {
        return mockMvc.perform(post("/poker/{gameId}/join", gameId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body()));
    }

    @Test
    @DisplayName("POST /poker/{gameId}/join -> 200 quando a entrada é válida")
    void shouldReturnOkWhenJoining() throws Exception {
        when(gameService.joinGame(gameId, playerId))
                .thenReturn(new JoinGameResponse(gameId, playerId));

        join().andExpect(status().isOk());
    }

    // Os status abaixo dependem do seu @RestControllerAdvice: ajuste se forem outros.

    @Test
    @DisplayName("-> 404 quando a mesa não existe")
    void shouldReturnNotFoundWhenGameMissing() throws Exception {
        when(gameService.joinGame(gameId, playerId)).thenThrow(new GameNotFoundException());

        join().andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("-> 404 quando o jogador não existe")
    void shouldReturnNotFoundWhenPlayerMissing() throws Exception {
        when(gameService.joinGame(gameId, playerId)).thenThrow(new PlayerNotFoundException());

        join().andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("-> 409 quando a partida já começou")
    void shouldReturnConflictWhenGameStarted() throws Exception {
        when(gameService.joinGame(gameId, playerId)).thenThrow(new GameAlreadyStartedException());

        join().andExpect(status().isConflict());
    }

    @Test
    @DisplayName("-> 400 quando a mesa está cheia")
    void shouldReturnConflictWhenGameFull() throws Exception {
        when(gameService.joinGame(gameId, playerId)).thenThrow(new GameFullException());

        join().andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("-> 409 quando o jogador já está na mesa")
    void shouldReturnConflictWhenPlayerAlreadyInGame() throws Exception {
        when(gameService.joinGame(gameId, playerId)).thenThrow(new PlayerAlreadyInGameException());

        join().andExpect(status().isConflict());
    }

    @Test
    @DisplayName("-> 400 quando o gameId não é um UUID válido")
    void shouldReturnBadRequestWhenGameIdInvalid() throws Exception {
        mockMvc.perform(post("/poker/{gameId}/join", "nao-e-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("-> 400 quando o corpo da requisição é JSON malformado")
    void shouldReturnBadRequestWhenBodyMalformed() throws Exception {
        mockMvc.perform(post("/poker/{gameId}/join", gameId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalido"))
                .andExpect(status().isBadRequest());
    }
}