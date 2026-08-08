package com.relieftrack.service;

import com.relieftrack.datastructure.avl.AVLTree;
import com.relieftrack.model.Inventory;
import com.relieftrack.repository.InventoryRepository;

import java.sql.SQLException;
import java.util.List;

public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private AVLTree<String, Inventory> inventoryTree = new AVLTree<>();

    public InventoryService() {
        this.inventoryRepository = new InventoryRepository();
        try {
            preloadInventoryTree();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void preloadInventoryTree() throws SQLException {
        inventoryTree = new AVLTree<>();
        List<Inventory> inventories = inventoryRepository.findAll();
        for (Inventory inv : inventories) {
            if (inv.getReliefItem() != null && inv.getReliefItem().getName() != null) {
                inventoryTree.put(inv.getReliefItem().getName().toLowerCase().trim(), inv);
            }
        }
    }

    public void save(Inventory inventory) throws SQLException {
        inventoryRepository.save(inventory);
        if (inventory.getReliefItem() != null && inventory.getReliefItem().getName() != null) {
            inventoryTree.put(inventory.getReliefItem().getName().toLowerCase().trim(), inventory);
        }
    }

    public void update(Inventory inventory) throws SQLException {
        inventoryRepository.update(inventory);
        if (inventory.getReliefItem() != null && inventory.getReliefItem().getName() != null) {
            inventoryTree.put(inventory.getReliefItem().getName().toLowerCase().trim(), inventory);
        }
    }

    public void delete(int id) throws SQLException {
        Inventory inventory = findById(id);
        if (inventory != null && inventory.getReliefItem() != null && inventory.getReliefItem().getName() != null) {
            inventoryTree.remove(inventory.getReliefItem().getName().toLowerCase().trim());
        }
        inventoryRepository.delete(id);
    }

    public Inventory findById(int id) throws SQLException {
        return inventoryRepository.findById(id);
    }

    public List<Inventory> findAll() throws SQLException {
        return inventoryRepository.findAll();
    }

    public List<Inventory> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return inventoryTree.valuesInOrder();
        }
        return inventoryTree.searchPrefix(query.toLowerCase().trim());
    }
}
