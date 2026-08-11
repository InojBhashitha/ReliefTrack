package com.relieftrack.repository;

import com.relieftrack.datastructure.hashtable.HashTable;
import com.relieftrack.enums.Role;
import com.relieftrack.model.User;
import com.relieftrack.repository.interfaces.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository extends BaseRepository implements Repository<User> {

    private final HashTable<String, User> userLookupTable = new HashTable<>();

    @Override
    public void save(User entity) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, full_name, role) VALUES (?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, entity.getUsername());
            statement.setString(2, entity.getPasswordHash());
            statement.setString(3, entity.getFullName());
            statement.setString(4, entity.getRole().name());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setUserId(generatedKeys.getInt(1));
                }
            }

            userLookupTable.put(entity.getUsername(), entity);
        }
    }

    @Override
    public void update(User entity) throws SQLException {
        // Look up the current username in the DB before applying the update,
        // so we can evict the old cache entry if the username changes.
        String oldUsername = null;
        User existingUser = findById(entity.getUserId());
        if (existingUser != null) {
            oldUsername = existingUser.getUsername();
        }

        String sql = "UPDATE users SET username = ?, password_hash = ?, full_name = ?, role = ? WHERE user_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, entity.getUsername());
            statement.setString(2, entity.getPasswordHash());
            statement.setString(3, entity.getFullName());
            statement.setString(4, entity.getRole().name());
            statement.setInt(5, entity.getUserId());
            statement.executeUpdate();

            // If the username changed, remove the stale old-username cache entry.
            if (oldUsername != null && !oldUsername.equals(entity.getUsername())) {
                userLookupTable.remove(oldUsername);
            }
            userLookupTable.put(entity.getUsername(), entity);
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        User user = findById(id);
        if (user != null) {
            userLookupTable.remove(user.getUsername());
        }

        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    @Override
    public User findById(int id) throws SQLException {
        String sql = "SELECT user_id, username, password_hash, full_name, role FROM users WHERE user_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapUser(resultSet);
                }
            }
        }

        return null;
    }

    public User findByUsername(String username) throws SQLException {
        if (userLookupTable.containsKey(username)) {
            return userLookupTable.get(username);
        }

        String sql = "SELECT user_id, username, password_hash, full_name, role FROM users WHERE username = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = mapUser(resultSet);
                    userLookupTable.put(username, user);
                    return user;
                }
            }
        }

        return null;
    }

    public Optional<User> authenticate(String username, String passwordHash) throws SQLException {
        if (userLookupTable.isEmpty()) {
            preloadLookupTable();
        }

        User cachedUser = userLookupTable.get(username);
        if (cachedUser != null && cachedUser.getPasswordHash().equals(passwordHash)) {
            return Optional.of(cachedUser);
        }

        User user = findByUsername(username);
        if (user != null && user.getPasswordHash().equals(passwordHash)) {
            userLookupTable.put(username, user);
            return Optional.of(user);
        }

        return Optional.empty();
    }

    public HashTable<String, User> getUserLookupTable() {
        return userLookupTable;
    }

    public void preloadLookupTable() throws SQLException {
        List<User> users = findAll();
        userLookupTable.clear();

        for (User user : users) {
            userLookupTable.put(user.getUsername(), user);
        }
    }

    @Override
    public List<User> findAll() throws SQLException {
        String sql = "SELECT user_id, username, password_hash, full_name, role FROM users";
        List<User> users = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                users.add(mapUser(resultSet));
            }
        }

        return users;
    }

    private User mapUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setUserId(resultSet.getInt("user_id"));
        user.setUsername(resultSet.getString("username"));
        user.setPasswordHash(resultSet.getString("password_hash"));
        user.setFullName(resultSet.getString("full_name"));
        user.setRole(Role.valueOf(resultSet.getString("role")));
        return user;
    }
}