package com.project.riverbed.domain.player;

import com.jayway.jsonpath.JsonPath;
import com.project.riverbed.domain.user.User;
import com.project.riverbed.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para o endpoint de cadastro de jogadores (POST /poker/cadastro).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PlayerRegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("CA01, CA04, CA06: Deve cadastrar novo jogador com dados válidos, retornar id e persistir no sistema")
    void shouldRegisterPlayerSuccessfullyAndReturnId() throws Exception {
        String requestJson = """
                {
                    "nome": "Jogador Riverbed",
                    "email": "jogador.riverbed@example.com",
                    "senha": "senhaSegura123"
                }
                """;

        MvcResult result = mockMvc.perform(post("/poker/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        String idString = JsonPath.read(responseBody, "$.id");
        UUID createdId = UUID.fromString(idString);

        // CA06: Após a criação bem-sucedida da conta, os dados do jogador devem permanecer registrados no sistema
        Optional<User> userOpt = userRepository.findById(createdId);
        assertTrue(userOpt.isPresent(), "O jogador deve estar salvo no banco de dados");

        User savedUser = userOpt.get();
        assertEquals("Jogador Riverbed", savedUser.getName());
        assertEquals("jogador.riverbed@example.com", savedUser.getEmail());
        assertNotNull(savedUser.getPassword());
        assertNotEquals("senhaSegura123", savedUser.getPassword(), "A senha não deve ser salva em texto puro");
        assertTrue(passwordEncoder.matches("senhaSegura123", savedUser.getPassword()), "A senha hasheada com Argon2 deve corresponder à original");
        assertNotNull(savedUser.getCreatedAt());
        assertNotNull(savedUser.getUpdatedAt());
    }

    @Test
    @DisplayName("Deve aceitar aliases em inglês (name, e-mail, password)")
    void shouldAcceptEnglishAliases() throws Exception {
        String requestJson = """
                {
                    "name": "Poker Pro",
                    "e-mail": "pro@example.com",
                    "password": "senhaForte456"
                }
                """;

        mockMvc.perform(post("/poker/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());

        Optional<User> userOpt = userRepository.findByEmail("pro@example.com");
        assertTrue(userOpt.isPresent());
        assertEquals("Poker Pro", userOpt.get().getName());
    }

    @Test
    @DisplayName("CA02, CA05: Deve rejeitar cadastro com nome obrigatório ausente ou em branco")
    void shouldRejectWhenNameIsMissingOrBlank() throws Exception {
        String requestJson = """
                {
                    "nome": "   ",
                    "email": "semnome@example.com",
                    "senha": "validPassword123"
                }
                """;

        mockMvc.perform(post("/poker/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.name").exists());
    }

    @Test
    @DisplayName("CA02, CA05: Deve rejeitar cadastro com e-mail obrigatório ausente ou em branco")
    void shouldRejectWhenEmailIsMissingOrBlank() throws Exception {
        String requestJson = """
                {
                    "nome": "Player Without Email",
                    "email": "",
                    "senha": "validPassword123"
                }
                """;

        mockMvc.perform(post("/poker/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.email").exists());
    }

    @Test
    @DisplayName("CA02, CA05: Deve rejeitar cadastro com formato de e-mail inválido")
    void shouldRejectWhenEmailIsInvalid() throws Exception {
        String requestJson = """
                {
                    "nome": "Player Bad Email",
                    "email": "invalid-email-format",
                    "senha": "validPassword123"
                }
                """;

        mockMvc.perform(post("/poker/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.email").exists());
    }

    @Test
    @DisplayName("CA02, CA05: Deve rejeitar cadastro com senha obrigatória ausente ou em branco")
    void shouldRejectWhenPasswordIsMissingOrBlank() throws Exception {
        String requestJson = """
                {
                    "nome": "Player Without Password",
                    "email": "nopass@example.com",
                    "senha": "   "
                }
                """;

        mockMvc.perform(post("/poker/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.password").exists());
    }

    @Test
    @DisplayName("CA02, CA05: Deve rejeitar cadastro com senha menor que 6 caracteres")
    void shouldRejectWhenPasswordIsTooShort() throws Exception {
        String requestJson = """
                {
                    "nome": "Player Short Password",
                    "email": "short@example.com",
                    "senha": "123"
                }
                """;

        mockMvc.perform(post("/poker/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.password").exists());
    }

    @Test
    @DisplayName("CA02, CA05: Deve rejeitar requisição com corpo JSON mal formatado")
    void shouldRejectWhenJsonIsMalformed() throws Exception {
        String malformedJson = """
                {
                    "nome": "Player",
                    "email":
                }
                """;

        mockMvc.perform(post("/poker/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Requisição inválida"));
    }

    @Test
    @DisplayName("CA03, CA05: Deve impedir o cadastro de mais de um jogador com o mesmo e-mail")
    void shouldPreventDuplicateEmailRegistration() throws Exception {
        String firstRegistration = """
                {
                    "nome": "First Player",
                    "email": "duplicate@example.com",
                    "senha": "validPassword123"
                }
                """;

        mockMvc.perform(post("/poker/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstRegistration))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());

        String secondRegistration = """
                {
                    "nome": "Second Player",
                    "email": "DUPLICATE@EXAMPLE.COM",
                    "senha": "anotherPassword456"
                }
                """;

        mockMvc.perform(post("/poker/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(secondRegistration))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("E-mail já cadastrado no sistema"));

        // Garante que apenas 1 jogador permanece cadastrado no banco
        assertEquals(1, userRepository.count());
    }
}
