package com.relieftrack.service;

import com.relieftrack.model.Warehouse;
import com.relieftrack.repository.WarehouseRepository;

import java.sql.SQLException;
import java.util.List;

public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    public WarehouseService() {
        this.warehouseRepository = new WarehouseRepository();
    }

    public void save(Warehouse warehouse) throws SQLException {
        warehouseRepository.save(warehouse);
    }

    public void update(Warehouse warehouse) throws SQLException {
        warehouseRepository.update(warehouse);
    }

    public void delete(int id) throws SQLException {
        warehouseRepository.delete(id);
    }

    public Warehouse findById(int id) throws SQLException {
        return warehouseRepository.findById(id);
    }

    public List<Warehouse> findAll() throws SQLException {
        return warehouseRepository.findAll();
    }
}
