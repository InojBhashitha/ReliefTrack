package com.relieftrack.repository;

import com.relieftrack.model.Warehouse;
import com.relieftrack.repository.interfaces.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WarehouseRepository extends BaseRepository implements Repository<Warehouse> {

    @Override
    public void save(Warehouse entity) throws SQLException {
        String sql = "INSERT INTO warehouses (name, district, address) VALUES (?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, entity.getName());
            statement.setString(2, entity.getDistrict());
            statement.setString(3, entity.getAddress());
            statement.executeUpdate();
        }
    }

    @Override
    public void update(Warehouse entity) throws SQLException {
        String sql = "UPDATE warehouses SET name = ?, district = ?, address = ? WHERE warehouse_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, entity.getName());
            statement.setString(2, entity.getDistrict());
            statement.setString(3, entity.getAddress());
            statement.setInt(4, entity.getWarehouseId());
            statement.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM warehouses WHERE warehouse_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    @Override
    public Warehouse findById(int id) throws SQLException {
        String sql = "SELECT warehouse_id, name, district, address FROM warehouses WHERE warehouse_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapWarehouse(resultSet);
                }
            }
        }

        return null;
    }

    @Override
    public List<Warehouse> findAll() throws SQLException {
        String sql = "SELECT warehouse_id, name, district, address FROM warehouses";
        List<Warehouse> warehouses = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                warehouses.add(mapWarehouse(resultSet));
            }
        }

        return warehouses;
    }

    private Warehouse mapWarehouse(ResultSet resultSet) throws SQLException {
        Warehouse warehouse = new Warehouse();
        warehouse.setWarehouseId(resultSet.getInt("warehouse_id"));
        warehouse.setName(resultSet.getString("name"));
        warehouse.setDistrict(resultSet.getString("district"));
        warehouse.setAddress(resultSet.getString("address"));
        return warehouse;
    }
}