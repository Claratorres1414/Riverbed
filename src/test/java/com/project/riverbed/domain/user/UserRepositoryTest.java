package com.project.riverbed.domain.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldPersistUser() {
        User user = User.builder()
                .username("test-user")
                .build();

        User savedUser = userRepository.saveAndFlush(user);

        assertNotNull(savedUser.getId());
        assertNotNull(savedUser.getCreatedAt());
        assertNotNull(savedUser.getUpdatedAt());
        assertEquals("test-user", savedUser.getUsername());
    }

    @Test
    void shouldNotPersistUserWithDuplicateUsername() {
        User firstUser = User.builder()
                .username("test-user")
                .build();

        User secondUser = User.builder()
                .username("test-user")
                .build();

        userRepository.saveAndFlush(firstUser);

        assertThrows(DataIntegrityViolationException.class, () ->
                userRepository.saveAndFlush(secondUser)
        );
    }

    @Test
    void shouldFindUserByUsername() {
        User user = User.builder()
                .username("test-user")
                .build();

        userRepository.saveAndFlush(user);

        Optional<User> foundUser = userRepository.findByUsername("test-user");

        assertTrue(foundUser.isPresent());
        assertEquals(user.getId(), foundUser.get().getId());
        assertEquals("test-user", foundUser.get().getUsername());
    }

    @Test
    void shouldReturnEmptyWhenUsernameDoesNotExist() {
        Optional<User> foundUser = userRepository.findByUsername("non-existent");

        assertTrue(foundUser.isEmpty());
    }
}