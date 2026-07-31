package com.relieftrack.repository;

import com.relieftrack.model.Dispatch;
import com.relieftrack.repository.interfaces.Repository;

import java.sql.SQLException;
import java.util.List;

public class DispatchRepository extends BaseRepository implements Repository<Dispatch> {

    @Override
    public void save(Dispatch entity) throws SQLException {

    }

    @Override
    public void update(Dispatch entity) throws SQLException {

    }

    @Override
    public void delete(int id) throws SQLException {

    }

    @Override
    public Dispatch findById(int id) throws SQLException {
        return null;
    }

    @Override
    public List<Dispatch> findAll() throws SQLException {
        return List.of();
    }
}