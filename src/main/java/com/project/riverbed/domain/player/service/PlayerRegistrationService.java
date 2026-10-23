package com.project.riverbed.domain.player.service;

import com.project.riverbed.domain.player.dto.PlayerRegistrationRequest;
import com.project.riverbed.domain.player.dto.PlayerRegistrationResponse;
import com.project.riverbed.domain.player.exception.EmailAlreadyExistsException;
import com.project.riverbed.domain.user.User;
import com.project.riverbed.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço responsável pela lógica de negócio do cadastro de jogadores.
 */
@Service
@RequiredArgsConstructor
public class PlayerRegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Valida os dados, impede e-mails duplicados, criptografa a senha e persiste o novo jogador.
     */
    @Transactional
    public PlayerRegistrationResponse register(PlayerRegistrationRequest request) {
        // Normaliza o e-mail para evitar duplicações por letras maiúsculas ou espaços
        String normalizedEmail = request.email().trim().toLowerCase();

        // RF04, CA03: Impede o cadastro de mais de um jogador com o mesmo e-mail
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyExistsException("E-mail já cadastrado no sistema");
        }

        // RF05, CA06: Registra os dados do novo jogador gerando identificador único
        User user = User.builder()
                .name(request.name().trim())
                .username(normalizedEmail)
                .email(normalizedEmail)
                .password(passwordEncoder.encode(request.password()))
                .build();

        User savedUser = userRepository.save(user);

        // RF07, CA04: Retorna o identificador (id) do jogador criado
        return new PlayerRegistrationResponse(savedUser.getId());
    }
}
