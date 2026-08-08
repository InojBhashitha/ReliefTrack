package com.relieftrack.repository;

import com.relieftrack.enums.Category;
import com.relieftrack.model.ReliefItem;
import com.relieftrack.repository.interfaces.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReliefItemRepository extends BaseRepository implements Repository<ReliefItem> {

    @Override
    public void save(ReliefItem item) throws SQLException {

        String sql = """
                INSERT INTO relief_items(name, category, expiry_date)
                VALUES (?, ?, ?)
                """;

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {

            statement.setString(1, item.getName());
            statement.setString(2, item.getCategory().name());
            statement.setString(3, item.getExpiryDate().toString());

            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    item.setItemId(keys.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(ReliefItem item) throws SQLException {

        String sql = """
                UPDATE relief_items
                SET name=?, category=?, expiry_date=?
                WHERE item_id=?
                """;

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, item.getName());
            statement.setString(2, item.getCategory().name());
            statement.setString(3, item.getExpiryDate().toString());
            statement.setInt(4, item.getItemId());

            statement.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {

        String sql = "DELETE FROM relief_items WHERE item_id=?";

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, id);

            statement.executeUpdate();
        }
    }

    @Override
    public ReliefItem findById(int id) throws SQLException {

        String sql = "SELECT * FROM relief_items WHERE item_id=?";

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, id);

<<<<<<< HEAD
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return new ReliefItem(
                            rs.getInt("item_id"),
                            rs.getString("name"),
                            Category.valueOf(rs.getString("category")),
                            LocalDate.parse(rs.getString("expiry_date"))
                    );
                }
=======
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {

                return new ReliefItem(
                        rs.getInt("item_id"),
                        rs.getString("name"),
                        parseCategory(rs.getString("category")),
                        parseDate(rs.getString("expiry_date"))
                );
>>>>>>> 2afd4e71317da30b0e88e536c5d15bf64fec63dd
            }
        }

        return null;
    }

    @Override
    public List<ReliefItem> findAll() throws SQLException {

        List<ReliefItem> items = new ArrayList<>();

        String sql = "SELECT * FROM relief_items";

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()
        ) {

            while (rs.next()) {

                ReliefItem item = new ReliefItem(
                        rs.getInt("item_id"),
                        rs.getString("name"),
                        parseCategory(rs.getString("category")),
                        parseDate(rs.getString("expiry_date"))
                );

                items.add(item);
            }
        }

        return items;
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

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return LocalDate.now();
        }
        try {
            return LocalDate.parse(value);
        } catch (Exception e) {
            return LocalDate.now();
        }
    }
}
