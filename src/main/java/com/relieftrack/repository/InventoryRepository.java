package com.relieftrack.repository;

import com.relieftrack.enums.Category;
import com.relieftrack.model.Inventory;
import com.relieftrack.model.ReliefItem;
import com.relieftrack.model.Warehouse;
import com.relieftrack.repository.interfaces.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InventoryRepository extends BaseRepository implements Repository<Inventory> {

    @Override
    public void save(Inventory entity) throws SQLException {
        String sql = "INSERT INTO inventory (warehouse_id, item_id, quantity, minimum_stock) VALUES (?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, entity.getWarehouse().getWarehouseId());
            statement.setInt(2, entity.getReliefItem().getItemId());
            statement.setInt(3, entity.getQuantity());
            statement.setInt(4, entity.getMinimumStock());
            statement.executeUpdate();
        }
    }

    @Override
    public void update(Inventory entity) throws SQLException {
        String sql = "UPDATE inventory SET warehouse_id = ?, item_id = ?, quantity = ?, minimum_stock = ? WHERE inventory_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, entity.getWarehouse().getWarehouseId());
            statement.setInt(2, entity.getReliefItem().getItemId());
            statement.setInt(3, entity.getQuantity());
            statement.setInt(4, entity.getMinimumStock());
            statement.setInt(5, entity.getInventoryId());
            statement.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM inventory WHERE inventory_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    @Override
    public Inventory findById(int id) throws SQLException {
        String sql = "SELECT i.inventory_id, i.warehouse_id, i.item_id, i.quantity, i.minimum_stock, " +
                "w.name AS warehouse_name, w.district, w.address, " +
                "r.name AS item_name, r.category, r.expiry_date " +
                "FROM inventory i " +
                "JOIN warehouses w ON i.warehouse_id = w.warehouse_id " +
                "JOIN relief_items r ON i.item_id = r.item_id " +
                "WHERE i.inventory_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapInventory(resultSet);
                }
            }
        }

        return null;
    }

    @Override
    public List<Inventory> findAll() throws SQLException {
        String sql = "SELECT i.inventory_id, i.warehouse_id, i.item_id, i.quantity, i.minimum_stock, " +
                "w.name AS warehouse_name, w.district, w.address, " +
                "r.name AS item_name, r.category, r.expiry_date " +
                "FROM inventory i " +
                "JOIN warehouses w ON i.warehouse_id = w.warehouse_id " +
                "JOIN relief_items r ON i.item_id = r.item_id";

        List<Inventory> inventories = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                inventories.add(mapInventory(resultSet));
            }
        }

        return inventories;
    }

    private Inventory mapInventory(ResultSet resultSet) throws SQLException {
        Warehouse warehouse = new Warehouse();
        warehouse.setWarehouseId(resultSet.getInt("warehouse_id"));
        warehouse.setName(resultSet.getString("warehouse_name"));
        warehouse.setDistrict(resultSet.getString("district"));
        warehouse.setAddress(resultSet.getString("address"));

        ReliefItem reliefItem = new ReliefItem();
        reliefItem.setItemId(resultSet.getInt("item_id"));
        reliefItem.setName(resultSet.getString("item_name"));
        reliefItem.setCategory(parseCategory(resultSet.getString("category")));
        reliefItem.setExpiryDate(parseDate(resultSet.getString("expiry_date")));

        Inventory inventory = new Inventory();
        inventory.setInventoryId(resultSet.getInt("inventory_id"));
        inventory.setWarehouse(warehouse);
        inventory.setReliefItem(reliefItem);
        inventory.setQuantity(resultSet.getInt("quantity"));
        inventory.setMinimumStock(resultSet.getInt("minimum_stock"));
        return inventory;
    }

    private Category parseCategory(String value) {
        if (value == null || value.isBlank()) {
            return Category.OTHER;
        }
        try {
            return Category.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return Category.OTHER;
        }
    }

    private java.time.LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return java.time.LocalDate.now();
        }
        try {
            return java.time.LocalDate.parse(value);
        } catch (Exception e) {
            return java.time.LocalDate.now();
        }
    }
}