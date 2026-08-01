package com.relieftrack.service;

import com.relieftrack.model.User;
import com.relieftrack.repository.UserRepository;

import java.sql.SQLException;
import java.util.List;

public class UserService {

    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    public void save(User user) throws SQLException {
        userRepository.save(user);
    }

    public void update(User user) throws SQLException {
        userRepository.update(user);
    }

    public void delete(int id) throws SQLException {
        userRepository.delete(id);
    }

    public List<User> findAll() throws SQLException {
        return userRepository.findAll();
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }
}
