package com.relieftrack.repository;

import com.relieftrack.enums.Category;
import com.relieftrack.enums.DisasterType;
import com.relieftrack.enums.PriorityLevel;
import com.relieftrack.enums.RequestStatus;
import com.relieftrack.model.EmergencyRequest;
import com.relieftrack.model.ReliefItem;
import com.relieftrack.repository.interfaces.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmergencyRequestRepository extends BaseRepository implements Repository<EmergencyRequest> {

    @Override
    public void save(EmergencyRequest entity) throws SQLException {
        String sql = "INSERT INTO emergency_requests (organization, disaster_type, item_id, quantity, priority, status, request_date) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, entity.getOrganization());
            statement.setString(2, entity.getDisasterType().name());
            statement.setInt(3, entity.getReliefItem().getItemId());
            statement.setInt(4, entity.getQuantity());
            statement.setString(5, entity.getPriority().name());
            statement.setString(6, entity.getStatus().name());
            statement.setString(7, entity.getRequestDate().toString());
            statement.executeUpdate();
        }
    }

    @Override
    public void update(EmergencyRequest entity) throws SQLException {
        String sql = "UPDATE emergency_requests SET organization = ?, disaster_type = ?, item_id = ?, quantity = ?, priority = ?, status = ?, request_date = ? WHERE request_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, entity.getOrganization());
            statement.setString(2, entity.getDisasterType().name());
            statement.setInt(3, entity.getReliefItem().getItemId());
            statement.setInt(4, entity.getQuantity());
            statement.setString(5, entity.getPriority().name());
            statement.setString(6, entity.getStatus().name());
            statement.setString(7, entity.getRequestDate().toString());
            statement.setInt(8, entity.getRequestId());
            statement.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM emergency_requests WHERE request_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    @Override
    public EmergencyRequest findById(int id) throws SQLException {
        String sql = "SELECT r.request_id, r.organization, r.disaster_type, r.item_id, r.quantity, r.priority, r.status, r.request_date, " +
                "i.name AS item_name, i.category, i.expiry_date " +
                "FROM emergency_requests r JOIN relief_items i ON r.item_id = i.item_id WHERE r.request_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRequest(resultSet);
                }
            }
        }

        return null;
    }

    @Override
    public List<EmergencyRequest> findAll() throws SQLException {
        String sql = "SELECT r.request_id, r.organization, r.disaster_type, r.item_id, r.quantity, r.priority, r.status, r.request_date, " +
                "i.name AS item_name, i.category, i.expiry_date " +
                "FROM emergency_requests r JOIN relief_items i ON r.item_id = i.item_id";

        List<EmergencyRequest> requests = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                requests.add(mapRequest(resultSet));
            }
        }

        return requests;
    }

    private EmergencyRequest mapRequest(ResultSet resultSet) throws SQLException {
        ReliefItem reliefItem = new ReliefItem();
        reliefItem.setItemId(resultSet.getInt("item_id"));
        reliefItem.setName(resultSet.getString("item_name"));
        reliefItem.setCategory(parseCategory(resultSet.getString("category")));
        reliefItem.setExpiryDate(parseDate(resultSet.getString("expiry_date")));

        EmergencyRequest request = new EmergencyRequest();
        request.setRequestId(resultSet.getInt("request_id"));
        request.setOrganization(resultSet.getString("organization"));
        request.setDisasterType(parseDisasterType(resultSet.getString("disaster_type")));
        request.setReliefItem(reliefItem);
        request.setQuantity(resultSet.getInt("quantity"));
        request.setPriority(parsePriority(resultSet.getString("priority")));
        request.setStatus(parseStatus(resultSet.getString("status")));
        request.setRequestDate(parseDateTime(resultSet.getString("request_date")));
        return request;
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

    private DisasterType parseDisasterType(String value) {
        if (value == null || value.isBlank()) {
            return DisasterType.OTHER;
        }
        try {
            return DisasterType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return DisasterType.OTHER;
        }
    }

    private PriorityLevel parsePriority(String value) {
        if (value == null || value.isBlank()) {
            return PriorityLevel.MEDIUM;
        }
        try {
            return PriorityLevel.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return PriorityLevel.MEDIUM;
        }
    }

    private RequestStatus parseStatus(String value) {
        if (value == null || value.isBlank()) {
            return RequestStatus.PENDING;
        }
        try {
            return RequestStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return RequestStatus.PENDING;
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

    private java.time.LocalDateTime parseDateTime(String value) {
        if (value == null || value.isBlank()) {
            return java.time.LocalDateTime.now();
        }
        try {
            return java.time.LocalDateTime.parse(value);
        } catch (Exception e) {
            return java.time.LocalDateTime.now();
        }
    }
}