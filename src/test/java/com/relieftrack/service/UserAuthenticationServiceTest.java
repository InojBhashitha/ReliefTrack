package com.relieftrack.service;

import com.relieftrack.database.DatabaseInitializer;
import com.relieftrack.enums.Role;
import com.relieftrack.model.User;
import com.relieftrack.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserAuthenticationServiceTest {

    @Test
    void authenticateUsesHashLookupCache(@TempDir Path temporaryDirectory) throws Exception {
        System.setProperty("relieftrack.database.url", "jdbc:sqlite:" + temporaryDirectory.resolve("authentication-test.db"));
        DatabaseInitializer.initializeDatabase();

        UserRepository repository = new UserRepository();
        String username = "hash_user_" + System.nanoTime();

        repository.save(new User(
                0,
                username,
                "hashed-password-123",
                "Hash User",
                Role.ADMIN
        ));

        Optional<User> authenticatedUser = repository.authenticate(username, "hashed-password-123");

        assertTrue(authenticatedUser.isPresent());
        assertEquals(username, authenticatedUser.get().getUsername());
        assertTrue(repository.getUserLookupTable().containsKey(username));
        System.clearProperty("relieftrack.database.url");
    }
}
