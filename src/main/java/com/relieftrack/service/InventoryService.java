package com.relieftrack.service;

import com.relieftrack.datastructure.avl.AVLTree;
import com.relieftrack.model.Inventory;
import com.relieftrack.repository.InventoryRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service providing inventory management operations backed by an in-memory
 * AVL Tree data structure for O(log n) lookups, sorted retrievals, and range searches.
 */
public class InventoryService {

    private final InventoryRepository inventoryRepository;
<<<<<<< HEAD
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
=======
    private final AVLTree<Integer, Inventory> inventoryByIdTree;
    private final AVLTree<String, Inventory> inventoryByNameTree;

    public InventoryService() {
        this.inventoryRepository = new InventoryRepository();
        this.inventoryByIdTree = new AVLTree<>();
        this.inventoryByNameTree = new AVLTree<>();
    }

    public InventoryService(InventoryRepository repository) {
        this.inventoryRepository = repository;
        this.inventoryByIdTree = new AVLTree<>();
        this.inventoryByNameTree = new AVLTree<>();
    }

    /** Ensures the in-memory AVL Trees are synchronized with the database. */
    public synchronized void syncFromDatabase() throws SQLException {
        inventoryByIdTree.clear();
        inventoryByNameTree.clear();
        List<Inventory> allItems = inventoryRepository.findAll();
        for (Inventory item : allItems) {
            indexInventory(item);
        }
    }

    private void indexInventory(Inventory inventory) {
        if (inventory == null) return;
        inventoryByIdTree.put(inventory.getInventoryId(), inventory);
        if (inventory.getReliefItem() != null && inventory.getReliefItem().getName() != null) {
            String key = inventory.getReliefItem().getName().toLowerCase() + "#" + inventory.getInventoryId();
            inventoryByNameTree.put(key, inventory);
        }
    }

    private void removeFromIndex(Inventory inventory) {
        if (inventory == null) return;
        inventoryByIdTree.remove(inventory.getInventoryId());
        if (inventory.getReliefItem() != null && inventory.getReliefItem().getName() != null) {
            String key = inventory.getReliefItem().getName().toLowerCase() + "#" + inventory.getInventoryId();
            inventoryByNameTree.remove(key);
>>>>>>> 2afd4e71317da30b0e88e536c5d15bf64fec63dd
        }
    }

    public void save(Inventory inventory) throws SQLException {
        inventoryRepository.save(inventory);
<<<<<<< HEAD
        if (inventory.getReliefItem() != null && inventory.getReliefItem().getName() != null) {
            inventoryTree.put(inventory.getReliefItem().getName().toLowerCase().trim(), inventory);
        }
=======
        syncFromDatabase();
>>>>>>> 2afd4e71317da30b0e88e536c5d15bf64fec63dd
    }

    public void update(Inventory inventory) throws SQLException {
        inventoryRepository.update(inventory);
<<<<<<< HEAD
        if (inventory.getReliefItem() != null && inventory.getReliefItem().getName() != null) {
            inventoryTree.put(inventory.getReliefItem().getName().toLowerCase().trim(), inventory);
        }
    }

    public void delete(int id) throws SQLException {
        Inventory inventory = findById(id);
        if (inventory != null && inventory.getReliefItem() != null && inventory.getReliefItem().getName() != null) {
            inventoryTree.remove(inventory.getReliefItem().getName().toLowerCase().trim());
        }
=======
        syncFromDatabase();
    }

    public void delete(int id) throws SQLException {
        Inventory existing = inventoryRepository.findById(id);
>>>>>>> 2afd4e71317da30b0e88e536c5d15bf64fec63dd
        inventoryRepository.delete(id);
        if (existing != null) {
            removeFromIndex(existing);
        }
        syncFromDatabase();
    }

    public Inventory findById(int id) throws SQLException {
        Inventory item = inventoryRepository.findById(id);
        if (item != null) {
            indexInventory(item);
        } else {
            inventoryByIdTree.remove(id);
        }
        return item;
    }

    public List<Inventory> findAll() throws SQLException {
        syncFromDatabase();
        return inventoryByNameTree.valuesInOrder();
    }

    /** Returns all inventory items sorted alphabetically by item name using AVL Tree in-order traversal. */
    public List<Inventory> findAllSortedByName() throws SQLException {
        return findAll();
    }

    /** Searches for inventory items by exact item name in O(log n) time using AVL Tree index. */
    public List<Inventory> findByItemName(String itemName) throws SQLException {
        if (itemName == null || itemName.trim().isEmpty()) {
            return findAll();
        }
        syncFromDatabase();
        String prefix = itemName.trim().toLowerCase();
        return inventoryByNameTree.rangeSearch(prefix + "#", prefix + "#\uffff");
    }

    /** Returns all low stock items (quantity <= minimumStock) retrieved via AVL Tree traversal. */
    public List<Inventory> findLowStockItems() throws SQLException {
        List<Inventory> all = findAll();
        List<Inventory> lowStock = new ArrayList<>();
        for (Inventory item : all) {
            if (item.getQuantity() <= item.getMinimumStock()) {
                lowStock.add(item);
            }
        }
        return lowStock;
    }

    public AVLTree<Integer, Inventory> getInventoryByIdTree() {
        return inventoryByIdTree;
    }

    public AVLTree<String, Inventory> getInventoryByNameTree() {
        return inventoryByNameTree;
    }

    public List<Inventory> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return inventoryTree.valuesInOrder();
        }
        return inventoryTree.searchPrefix(query.toLowerCase().trim());
    }
}
