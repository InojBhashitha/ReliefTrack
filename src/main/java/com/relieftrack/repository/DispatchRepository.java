package com.relieftrack.repository;

import com.relieftrack.enums.DispatchStatus;
import com.relieftrack.enums.Category;
import com.relieftrack.enums.DisasterType;
import com.relieftrack.enums.PriorityLevel;
import com.relieftrack.enums.RequestStatus;
import com.relieftrack.model.Dispatch;
import com.relieftrack.model.EmergencyRequest;
import com.relieftrack.model.ReliefItem;
import com.relieftrack.model.Warehouse;
import com.relieftrack.repository.interfaces.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DispatchRepository extends BaseRepository implements Repository<Dispatch> {

    @Override
    public void save(Dispatch entity) throws SQLException {
        String sql = "INSERT INTO dispatches (request_id, warehouse_id, dispatch_date, status) VALUES (?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, entity.getRequest().getRequestId());
            statement.setInt(2, entity.getWarehouse().getWarehouseId());
            statement.setString(3, entity.getDispatchDate().toString());
            statement.setString(4, entity.getStatus().name());
            statement.executeUpdate();
        }
    }

    @Override
    public void update(Dispatch entity) throws SQLException {
        String sql = "UPDATE dispatches SET request_id = ?, warehouse_id = ?, dispatch_date = ?, status = ? WHERE dispatch_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, entity.getRequest().getRequestId());
            statement.setInt(2, entity.getWarehouse().getWarehouseId());
            statement.setString(3, entity.getDispatchDate().toString());
            statement.setString(4, entity.getStatus().name());
            statement.setInt(5, entity.getDispatchId());
            statement.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM dispatches WHERE dispatch_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    @Override
    public Dispatch findById(int id) throws SQLException {
        String sql = "SELECT d.dispatch_id, d.request_id, d.warehouse_id, d.dispatch_date, d.status, " +
                "w.name AS warehouse_name, w.district, w.address, " +
                "r.organization, r.disaster_type, r.item_id, r.quantity, r.priority, r.status AS request_status, r.request_date, " +
                "i.name AS item_name, i.category, i.expiry_date " +
                "FROM dispatches d " +
                "JOIN warehouses w ON d.warehouse_id = w.warehouse_id " +
                "JOIN emergency_requests r ON d.request_id = r.request_id " +
                "JOIN relief_items i ON r.item_id = i.item_id " +
                "WHERE d.dispatch_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapDispatch(resultSet);
                }
            }
        }

        return null;
    }

    @Override
    public List<Dispatch> findAll() throws SQLException {
        String sql = "SELECT d.dispatch_id, d.request_id, d.warehouse_id, d.dispatch_date, d.status, " +
                "w.name AS warehouse_name, w.district, w.address, " +
                "r.organization, r.disaster_type, r.item_id, r.quantity, r.priority, r.status AS request_status, r.request_date, " +
                "i.name AS item_name, i.category, i.expiry_date " +
                "FROM dispatches d " +
                "JOIN warehouses w ON d.warehouse_id = w.warehouse_id " +
                "JOIN emergency_requests r ON d.request_id = r.request_id " +
                "JOIN relief_items i ON r.item_id = i.item_id";

        List<Dispatch> dispatches = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                dispatches.add(mapDispatch(resultSet));
            }
        }

        return dispatches;
    }

    private Dispatch mapDispatch(ResultSet resultSet) throws SQLException {
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

        EmergencyRequest request = new EmergencyRequest();
        request.setRequestId(resultSet.getInt("request_id"));
        request.setOrganization(resultSet.getString("organization"));
        request.setDisasterType(parseDisasterType(resultSet.getString("disaster_type")));
        request.setReliefItem(reliefItem);
        request.setQuantity(resultSet.getInt("quantity"));
        request.setPriority(parsePriority(resultSet.getString("priority")));
        request.setStatus(parseStatus(resultSet.getString("request_status")));
        request.setRequestDate(parseDateTime(resultSet.getString("request_date")));

        Dispatch dispatch = new Dispatch();
        dispatch.setDispatchId(resultSet.getInt("dispatch_id"));
        dispatch.setRequest(request);
        dispatch.setWarehouse(warehouse);
        dispatch.setDispatchDate(parseDateTime(resultSet.getString("dispatch_date")));
        dispatch.setStatus(parseDispatchStatus(resultSet.getString("status")));
        return dispatch;
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

    private DispatchStatus parseDispatchStatus(String value) {
        if (value == null || value.isBlank()) {
            return DispatchStatus.PENDING;
        }
        try {
            return DispatchStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return DispatchStatus.PENDING;
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