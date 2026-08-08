package com.relieftrack.repository;

import com.relieftrack.database.DatabaseInitializer;
import com.relieftrack.enums.Role;
import com.relieftrack.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {

    @Test
    void testSaveUpdatesUserIdAndCachingWorksAndEvictsOnDelete(@TempDir Path tempDir) throws SQLException {
        System.setProperty("relieftrack.database.url", "jdbc:sqlite:" + tempDir.resolve("user-test.db"));
        DatabaseInitializer.initializeDatabase();

        UserRepository repository = new UserRepository();
        User user = new User(0, "test_user", "password_hash", "Test User", Role.ADMIN);

        // Initially ID is 0
        assertEquals(0, user.getUserId());

        // Save
        repository.save(user);

        // After save, the ID should be updated to a positive number
        assertTrue(user.getUserId() > 0);

        // Verify caching works
        assertTrue(repository.getUserLookupTable().containsKey("test_user"));

        // Delete user
        repository.delete(user.getUserId());

        // Cache must be evicted
        assertFalse(repository.getUserLookupTable().containsKey("test_user"));

        System.clearProperty("relieftrack.database.url");
    }
}
