package com.project.riverbed.shared.exception;

import com.project.riverbed.domain.game.exceptions.GameAlreadyStartedException;
import com.project.riverbed.domain.game.exceptions.GameFullException;
import com.project.riverbed.domain.game.exceptions.GameNotFoundException;
import com.project.riverbed.domain.game.exceptions.InsufficientPlayersException;
import com.project.riverbed.domain.game.exceptions.PlayerAlreadyInGameException;
import com.project.riverbed.domain.game.exceptions.UnauthorizedGameStartException;
import com.project.riverbed.domain.player.exception.EmailAlreadyExistsException;
import com.project.riverbed.domain.player.exception.PlayerNotFoundException;
import com.project.riverbed.shared.response.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex
    ) {
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage(),
                        (existing, replacement) -> existing
                ));

        ValidationErrorResponse response = new ValidationErrorResponse(
                400,
                "Validation failed",
                errors
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(InsufficientPlayersException.class)
    public ResponseEntity<ApiResponse<Void>> handleInsufficientPlayers(
            InsufficientPlayersException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        HttpStatus.BAD_REQUEST.value(),
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(
            NoResourceFoundException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(
                        HttpStatus.NOT_FOUND.value(),
                        "Recurso não encontrado"
                ));
    }

    @ExceptionHandler(GameNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleGameNotFound(
            GameNotFoundException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(
                        HttpStatus.NOT_FOUND.value(),
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(PlayerNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handlePlayerNotFound(
            PlayerNotFoundException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(
                        HttpStatus.NOT_FOUND.value(),
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(
            HttpMessageNotReadableException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        HttpStatus.BAD_REQUEST.value(),
                        "Requisição inválida"
                ));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.error(
                        HttpStatus.METHOD_NOT_ALLOWED.value(),
                        "Método HTTP não permitido"
                ));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnsupportedMediaType(
            HttpMediaTypeNotSupportedException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(ApiResponse.error(
                        HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                        "Tipo de mídia não suportado"
                ));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleEmailAlreadyExists(
            EmailAlreadyExistsException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(
                        HttpStatus.CONFLICT.value(),
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(
            DataIntegrityViolationException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(
                        HttpStatus.CONFLICT.value(),
                        "Conflito de dados: registro já existente no sistema"
                ));
    }

    @ExceptionHandler(UnauthorizedGameStartException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorizedGameStart(
            UnauthorizedGameStartException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(
                        HttpStatus.FORBIDDEN.value(),
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(GameAlreadyStartedException.class)
    public ResponseEntity<ApiResponse<Void>> handleGameAlreadyStarted(
            GameAlreadyStartedException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(
                        HttpStatus.CONFLICT.value(),
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleInternalServerError(
            Exception exception
    ) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Erro interno do servidor"
                ));
    }
    @ExceptionHandler(GameFullException.class)
    public ResponseEntity<ApiResponse<Void>> handleGameFull(GameFullException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        HttpStatus.BAD_REQUEST.value(),
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(PlayerAlreadyInGameException.class)
    public ResponseEntity<ApiResponse<Void>> handlePlayerAlreadyInGame(PlayerAlreadyInGameException exception) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(
                        HttpStatus.CONFLICT.value(),
                        exception.getMessage()
                ));
    }
}