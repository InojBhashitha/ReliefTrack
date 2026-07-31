package com.relieftrack.repository;

import com.relieftrack.model.Inventory;
import com.relieftrack.repository.interfaces.Repository;

import java.sql.SQLException;
import java.util.List;

public class InventoryRepository extends BaseRepository implements Repository<Inventory> {

    @Override
    public void save(Inventory entity) throws SQLException {

    }

    @Override
    public void update(Inventory entity) throws SQLException {

    }

    @Override
    public void delete(int id) throws SQLException {

    }

    @Override
    public Inventory findById(int id) throws SQLException {
        return null;
    }

    @Override
    public List<Inventory> findAll() throws SQLException {
        return List.of();
    }
}