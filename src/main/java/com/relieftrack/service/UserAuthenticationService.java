package com.relieftrack.service;

import com.relieftrack.model.User;
import com.relieftrack.repository.UserRepository;

import java.sql.SQLException;
import java.util.Optional;

public class UserAuthenticationService {

    private final UserRepository userRepository;

    public UserAuthenticationService() {
        this.userRepository = new UserRepository();
    }

    public Optional<User> authenticate(String username, String passwordHash) throws SQLException {
        return userRepository.authenticate(username, passwordHash);
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }
}
