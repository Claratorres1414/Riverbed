package com.project.riverbed.security.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PasswordConfigTest {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void shouldHashAndVerifyPassword() {
        String rawPassword = "Senha123";
        String hash = passwordEncoder.encode(rawPassword);

        assertNotEquals(rawPassword, hash);
        assertTrue(passwordEncoder.matches(rawPassword, hash));
        assertFalse(passwordEncoder.matches("senha-errada", hash));
    }

    @Test
    void shouldGenerateDifferentHashesForSamePassword() {
        String rawPassword = "MySecurePassword123!";

        String hash1 = passwordEncoder.encode(rawPassword);
        String hash2 = passwordEncoder.encode(rawPassword);

        assertNotEquals(hash1, hash2);

        assertTrue(passwordEncoder.matches(rawPassword, hash1));
        assertTrue(passwordEncoder.matches(rawPassword, hash2));
    }
}
