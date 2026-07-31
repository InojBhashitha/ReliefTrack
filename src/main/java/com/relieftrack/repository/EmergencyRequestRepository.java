package com.relieftrack.repository;

import com.relieftrack.model.EmergencyRequest;
import com.relieftrack.repository.interfaces.Repository;

import java.sql.SQLException;
import java.util.List;

public class EmergencyRequestRepository extends BaseRepository implements Repository<EmergencyRequest> {

    @Override
    public void save(EmergencyRequest entity) throws SQLException {

    }

    @Override
    public void update(EmergencyRequest entity) throws SQLException {

    }

    @Override
    public void delete(int id) throws SQLException {

    }

    @Override
    public EmergencyRequest findById(int id) throws SQLException {
        return null;
    }

    @Override
    public List<EmergencyRequest> findAll() throws SQLException {
        return List.of();
    }
}