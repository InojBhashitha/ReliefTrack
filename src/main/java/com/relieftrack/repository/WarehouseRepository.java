package com.relieftrack.repository;

import com.relieftrack.model.Warehouse;
import com.relieftrack.repository.interfaces.Repository;

import java.sql.SQLException;
import java.util.List;

public class WarehouseRepository extends BaseRepository implements Repository<Warehouse> {

    @Override
    public void save(Warehouse entity) throws SQLException {

    }

    @Override
    public void update(Warehouse entity) throws SQLException {

    }

    @Override
    public void delete(int id) throws SQLException {

    }

    @Override
    public Warehouse findById(int id) throws SQLException {
        return null;
    }

    @Override
    public List<Warehouse> findAll() throws SQLException {
        return List.of();
    }
}