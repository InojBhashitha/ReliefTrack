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

    @Test
    void testUpdateUsernameEvictsOldCacheEntry(@TempDir Path tempDir) throws SQLException {
        System.setProperty("relieftrack.database.url", "jdbc:sqlite:" + tempDir.resolve("cache-test.db"));
        DatabaseInitializer.initializeDatabase();

        UserRepository repository = new UserRepository();
        User user = new User(0, "old_name", "hash123", "Full Name", Role.ADMIN);
        repository.save(user);

        // Cache should contain old_name
        assertTrue(repository.getUserLookupTable().containsKey("old_name"));

        // Update username
        user.setUsername("new_name");
        repository.update(user);

        // Old username must be evicted from cache
        assertFalse(repository.getUserLookupTable().containsKey("old_name"),
                "Old username should be removed from cache after update.");
        // New username must be in cache
        assertTrue(repository.getUserLookupTable().containsKey("new_name"),
                "New username should be in cache after update.");
        // Cached user should be the same object
        assertEquals("new_name", repository.getUserLookupTable().get("new_name").getUsername());

        System.clearProperty("relieftrack.database.url");
    }

    @Test
    void testUpdateWithoutUsernameChangeKeepsCacheCorrect(@TempDir Path tempDir) throws SQLException {
        System.setProperty("relieftrack.database.url", "jdbc:sqlite:" + tempDir.resolve("cache-nochange-test.db"));
        DatabaseInitializer.initializeDatabase();

        UserRepository repository = new UserRepository();
        User user = new User(0, "stable_user", "hash123", "Original Name", Role.ADMIN);
        repository.save(user);

        // Update full name only, username stays the same
        user.setFullName("Updated Name");
        repository.update(user);

        // Cache should still contain the username
        assertTrue(repository.getUserLookupTable().containsKey("stable_user"));
        assertEquals("Updated Name", repository.getUserLookupTable().get("stable_user").getFullName());

        System.clearProperty("relieftrack.database.url");
    }

    @Test
    void testOldUsernameNoLongerResolvesAfterUpdate(@TempDir Path tempDir) throws SQLException {
        System.setProperty("relieftrack.database.url", "jdbc:sqlite:" + tempDir.resolve("lookup-test.db"));
        DatabaseInitializer.initializeDatabase();

        UserRepository repository = new UserRepository();
        User user = new User(0, "lookup_old", "hash123", "Lookup User", Role.ADMIN);
        repository.save(user);

        // Update username
        user.setUsername("lookup_new");
        repository.update(user);

        // findByUsername with old name should not return the user from cache
        assertNull(repository.getUserLookupTable().get("lookup_old"),
                "Old username should not resolve from cache.");

        // findByUsername with new name should work
        User found = repository.findByUsername("lookup_new");
        assertNotNull(found);
        assertEquals("lookup_new", found.getUsername());

        System.clearProperty("relieftrack.database.url");
    }
}
