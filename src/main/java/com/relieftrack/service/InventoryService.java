package com.relieftrack.service;

import com.relieftrack.model.Inventory;
import com.relieftrack.repository.InventoryRepository;

import java.sql.SQLException;
import java.util.List;

public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService() {
        this.inventoryRepository = new InventoryRepository();
    }

    public void save(Inventory inventory) throws SQLException {
        inventoryRepository.save(inventory);
    }

    public void update(Inventory inventory) throws SQLException {
        inventoryRepository.update(inventory);
    }

    public void delete(int id) throws SQLException {
        inventoryRepository.delete(id);
    }

    public Inventory findById(int id) throws SQLException {
        return inventoryRepository.findById(id);
    }

    public List<Inventory> findAll() throws SQLException {
        return inventoryRepository.findAll();
    }
}
