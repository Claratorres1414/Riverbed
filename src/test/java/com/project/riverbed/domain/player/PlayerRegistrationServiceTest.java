package com.project.riverbed.domain.player;

import com.project.riverbed.domain.player.dto.PlayerRegistrationRequest;
import com.project.riverbed.domain.player.dto.PlayerRegistrationResponse;
import com.project.riverbed.domain.player.exception.EmailAlreadyExistsException;
import com.project.riverbed.domain.player.service.PlayerRegistrationService;
import com.project.riverbed.domain.user.User;
import com.project.riverbed.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerRegistrationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PlayerRegistrationService playerRegistrationService;

    private PlayerRegistrationRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new PlayerRegistrationRequest(
                "Player One",
                "player1@example.com",
                "securePassword123"
        );
    }

    @Test
    @DisplayName("Deve cadastrar jogador com sucesso quando os dados forem válidos")
    void shouldRegisterPlayerSuccessfully() {
        UUID expectedId = UUID.randomUUID();
        when(userRepository.existsByEmail("player1@example.com")).thenReturn(false);
        when(passwordEncoder.encode("securePassword123")).thenReturn("hashedPassword123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(expectedId);
            return user;
        });

        PlayerRegistrationResponse response = playerRegistrationService.register(validRequest);

        assertNotNull(response);
        assertEquals(expectedId, response.id());

        // Verifica se os dados foram repassados corretamente para a entidade persistida
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();

        assertEquals("Player One", savedUser.getName());
        assertEquals("player1@example.com", savedUser.getEmail());
        assertEquals("hashedPassword123", savedUser.getPassword());
    }

    @Test
    @DisplayName("Deve lançar EmailAlreadyExistsException quando o e-mail já estiver cadastrado")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail("player1@example.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () ->
                playerRegistrationService.register(validRequest)
        );

        // Garante que não tenta persistir caso o e-mail já exista
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve normalizar o e-mail para letras minúsculas e sem espaços nas pontas")
    void shouldNormalizeEmailWhenRegistering() {
        PlayerRegistrationRequest request = new PlayerRegistrationRequest(
                "Player Two",
                "  PLAYER2@EXAMPLE.COM  ",
                "password123"
        );

        when(userRepository.existsByEmail("player2@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(UUID.randomUUID());
            return user;
        });

        playerRegistrationService.register(request);

        verify(userRepository).existsByEmail("player2@example.com");
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("player2@example.com", captor.getValue().getEmail());
    }
}
