package com.relieftrack.service;

import com.relieftrack.datastructure.avl.AVLTree;
import com.relieftrack.model.ReliefItem;
import com.relieftrack.repository.ReliefItemRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Service managing relief items using an in-memory AVL Tree index.
 */
public class ReliefItemService {
    private final ReliefItemRepository reliefItemRepository;
    private final AVLTree<String, ReliefItem> itemByNameTree = new AVLTree<>();
    private boolean isInitialized = false;

    public ReliefItemService() {
        this.reliefItemRepository = new ReliefItemRepository();
    }

    public ReliefItemService(ReliefItemRepository repository) {
        this.reliefItemRepository = repository;
    }

    public synchronized void syncFromDatabase() throws SQLException {
        itemByNameTree.clear();
        List<ReliefItem> items = reliefItemRepository.findAll();
        for (ReliefItem item : items) {
            if (item.getName() != null) {
                itemByNameTree.put(item.getName().toLowerCase(), item);
            }
        }
        isInitialized = true;
    }

    public void save(ReliefItem item) throws SQLException {
        reliefItemRepository.save(item);
        syncFromDatabase();
    }

    public void update(ReliefItem item) throws SQLException {
        reliefItemRepository.update(item);
        syncFromDatabase();
    }

    public List<ReliefItem> findAll() throws SQLException {
        if (!isInitialized) {
            syncFromDatabase();
        }
        return itemByNameTree.valuesInOrder();
    }

    public Optional<ReliefItem> findByName(String name) throws SQLException {
        if (name == null) return Optional.empty();
        if (!isInitialized) {
            syncFromDatabase();
        }
        return itemByNameTree.get(name.toLowerCase());
    }

    public AVLTree<String, ReliefItem> getItemByNameTree() {
        return itemByNameTree;
    }
}
