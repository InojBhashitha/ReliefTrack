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
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, item.getName());
            statement.setString(2, item.getCategory().name());
            statement.setString(3, item.getExpiryDate().toString());

            statement.executeUpdate();
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

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {

                return new ReliefItem(
                        rs.getInt("item_id"),
                        rs.getString("name"),
                        Category.valueOf(rs.getString("category")),
                        LocalDate.parse(rs.getString("expiry_date"))
                );
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
                        Category.valueOf(rs.getString("category")),
                        LocalDate.parse(rs.getString("expiry_date"))
                );

                items.add(item);
            }
        }

        return items;
    }
}