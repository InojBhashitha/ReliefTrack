package com.relieftrack.service;

import com.relieftrack.database.DatabaseManager;
import com.relieftrack.enums.DispatchStatus;
import com.relieftrack.enums.RequestStatus;
import com.relieftrack.model.Dispatch;
import com.relieftrack.model.EmergencyRequest;
import com.relieftrack.model.Warehouse;
import com.relieftrack.repository.DispatchRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class DispatchService {

    private final DispatchRepository dispatchRepository;

    public DispatchService() {
        this.dispatchRepository = new DispatchRepository();
    }

    public void save(Dispatch dispatch) throws SQLException {
        dispatchRepository.save(dispatch);
    }

    public void update(Dispatch dispatch) throws SQLException {
        dispatchRepository.update(dispatch);
    }

    public void delete(int id) throws SQLException {
        dispatchRepository.delete(id);
    }

    public Dispatch findById(int id) throws SQLException {
        return dispatchRepository.findById(id);
    }

    public List<Dispatch> findAll() throws SQLException {
        return dispatchRepository.findAll();
    }

    /**
     * Reserves inventory and schedules a dispatch as one database transaction.
     * A failed validation leaves inventory and request status unchanged.
     *
     * <p>The current request status is re-read from the database inside the
     * transaction to prevent stale objects from bypassing the status check.
     * An active-dispatch check prevents the same request from being dispatched
     * more than once while a previous dispatch is still pending or in transit.</p>
     */
    public void scheduleDispatch(EmergencyRequest request, Warehouse warehouse) throws SQLException {
        if (request == null || warehouse == null) {
            throw new IllegalArgumentException("Select both an emergency request and a warehouse.");
        }
        // Fast-fail on obviously invalid in-memory status (not authoritative)
        if (request.getStatus() != RequestStatus.PENDING && request.getStatus() != RequestStatus.APPROVED) {
            throw new IllegalStateException("Only pending or approved requests can be scheduled.");
        }

        try (Connection connection = DatabaseManager.getConnection()) {
            connection.setAutoCommit(false);
            try {
                // Re-read the current request status from the database
                RequestStatus currentStatus = findCurrentRequestStatus(connection, request.getRequestId());
                if (currentStatus == null) {
                    throw new IllegalStateException("Emergency request no longer exists.");
                }
                if (currentStatus != RequestStatus.PENDING && currentStatus != RequestStatus.APPROVED) {
                    throw new IllegalStateException("Only pending or approved requests can be scheduled. Current status: " + currentStatus + ".");
                }

                // Check for an existing active dispatch for this request
                if (hasActiveDispatch(connection, request.getRequestId())) {
                    throw new IllegalStateException("This request already has an active dispatch (pending or in transit).");
                }

                int availableStock = findAvailableStock(connection, warehouse.getWarehouseId(), request.getReliefItem().getItemId());
                if (availableStock < request.getQuantity()) {
                    throw new IllegalStateException("Insufficient stock at " + warehouse.getName() + ". Available: " + availableStock + ".");
                }

                reserveStock(connection, warehouse.getWarehouseId(), request.getReliefItem().getItemId(), request.getQuantity());
                insertDispatch(connection, request.getRequestId(), warehouse.getWarehouseId());
                updateRequestStatus(connection, request.getRequestId(), RequestStatus.DISPATCHED);
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                // Translate unique-constraint violations on the active-dispatch index
                // into a clear business error instead of exposing a raw SQLite exception.
                if (e.getMessage() != null && e.getMessage().contains("SQLITE_CONSTRAINT_UNIQUE")) {
                    throw new IllegalStateException("This request already has an active dispatch (pending or in transit).");
                }
                throw e;
            } catch (RuntimeException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private RequestStatus findCurrentRequestStatus(Connection connection, int requestId) throws SQLException {
        String sql = "SELECT status FROM emergency_requests WHERE request_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, requestId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    try {
                        return RequestStatus.valueOf(resultSet.getString("status").trim().toUpperCase());
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    private boolean hasActiveDispatch(Connection connection, int requestId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM dispatches WHERE request_id = ? AND status IN (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, requestId);
            statement.setString(2, DispatchStatus.PENDING.name());
            statement.setString(3, DispatchStatus.IN_TRANSIT.name());
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        }
    }

    private int findAvailableStock(Connection connection, int warehouseId, int itemId) throws SQLException {
        String sql = "SELECT quantity FROM inventory WHERE warehouse_id = ? AND item_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, warehouseId);
            statement.setInt(2, itemId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt("quantity") : 0;
            }
        }
    }

    private void reserveStock(Connection connection, int warehouseId, int itemId, int requestedQuantity) throws SQLException {
        String sql = "UPDATE inventory SET quantity = quantity - ? WHERE warehouse_id = ? AND item_id = ? AND quantity >= ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, requestedQuantity);
            statement.setInt(2, warehouseId);
            statement.setInt(3, itemId);
            statement.setInt(4, requestedQuantity);
            if (statement.executeUpdate() != 1) {
                throw new IllegalStateException("Stock changed while scheduling. Please try again.");
            }
        }
    }

    private void insertDispatch(Connection connection, int requestId, int warehouseId) throws SQLException {
        String sql = "INSERT INTO dispatches (request_id, warehouse_id, dispatch_date, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, requestId);
            statement.setInt(2, warehouseId);
            statement.setString(3, LocalDateTime.now().toString());
            statement.setString(4, DispatchStatus.PENDING.name());
            statement.executeUpdate();
        }
    }

    private void updateRequestStatus(Connection connection, int requestId, RequestStatus status) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE emergency_requests SET status = ? WHERE request_id = ?")) {
            statement.setString(1, status.name());
            statement.setInt(2, requestId);
            statement.executeUpdate();
        }
    }
}

