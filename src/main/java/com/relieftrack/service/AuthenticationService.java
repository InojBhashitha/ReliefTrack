package com.relieftrack.service;

import com.relieftrack.model.User;
import com.relieftrack.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HexFormat;
import java.util.Optional;

public class AuthenticationService {

    private static User currentUser;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static void clearSession() {
        currentUser = null;
    }

    private final UserRepository userRepository;

    public AuthenticationService() {
        this.userRepository = new UserRepository();
    }

    public Optional<User> authenticate(String username, String rawPassword) throws SQLException {
        String passwordHash = hashPassword(rawPassword);
        return userRepository.authenticate(username, passwordHash);
    }

    public String hashPassword(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm is not available", e);
        }
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }
}
