package com.relieftrack.repository;

import com.relieftrack.model.User;
import com.relieftrack.repository.interfaces.Repository;

import java.sql.SQLException;
import java.util.List;

public class UserRepository extends BaseRepository implements Repository<User> {

    @Override
    public void save(User entity) throws SQLException {

    }

    @Override
    public void update(User entity) throws SQLException {

    }

    @Override
    public void delete(int id) throws SQLException {

    }

    @Override
    public User findById(int id) throws SQLException {
        return null;
    }

    @Override
    public List<User> findAll() throws SQLException {
        return List.of();
    }
}