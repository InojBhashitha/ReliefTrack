package com.relieftrack.service;

import com.relieftrack.database.DatabaseManager;
import com.relieftrack.datastructure.graph.Graph;
import com.relieftrack.datastructure.graph.Vertex;
import com.relieftrack.model.Warehouse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WarehouseRoutingService {

    private final WarehouseService warehouseService = new WarehouseService();

    public Graph buildWarehouseGraph() throws SQLException {
        Graph graph = new Graph();
        List<Warehouse> warehouses = warehouseService.findAll();

        for (Warehouse warehouse : warehouses) {
            graph.addVertex(new Vertex(String.valueOf(warehouse.getWarehouseId()), warehouse.getName()));
        }

        try (Connection connection = DatabaseManager.getConnection()) {
            String sql = "SELECT source_warehouse, destination_warehouse, distance FROM warehouse_connections";
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Vertex source = findVertex(graph, resultSet.getInt("source_warehouse"));
                    Vertex destination = findVertex(graph, resultSet.getInt("destination_warehouse"));
                    if (source != null && destination != null) {
                        graph.addUndirectedEdge(source, destination, resultSet.getDouble("distance"));
                    }
                }
            }
        }

        return graph;
    }

    public List<String> findShortestRoute(int sourceWarehouseId, int destinationWarehouseId) throws SQLException {
        Graph graph = buildWarehouseGraph();
        Vertex source = findVertex(graph, sourceWarehouseId);
        Vertex destination = findVertex(graph, destinationWarehouseId);

        if (source == null || destination == null) {
            return List.of();
        }

        List<Vertex> path = graph.getShortestPath(source, destination);
        List<String> route = new ArrayList<>();
        for (Vertex vertex : path) {
            route.add(vertex.getName());
        }
        return route;
    }

    public String describeRoute(int sourceWarehouseId, int destinationWarehouseId) throws SQLException {
        List<String> route = findShortestRoute(sourceWarehouseId, destinationWarehouseId);
        if (route.isEmpty()) {
            return "No route found.";
        }
        return String.join(" -> ", route);
    }

    public int countConnections() throws SQLException {
        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM warehouse_connections")) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    return resultSet.next() ? resultSet.getInt(1) : 0;
                }
            }
        }
    }

    private Vertex findVertex(Graph graph, int warehouseId) {
        for (Vertex vertex : graph.getVertices()) {
            if (vertex.getId().equals(String.valueOf(warehouseId))) {
                return vertex;
            }
        }
        return null;
    }
}
