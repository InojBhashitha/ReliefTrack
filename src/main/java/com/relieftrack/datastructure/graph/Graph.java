package com.relieftrack.datastructure.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class Graph {
    private final Map<Vertex, List<Edge>> adjacencyList;

    public Graph() {
        this.adjacencyList = new HashMap<>();
    }

    public void addVertex(Vertex vertex) {
        if (vertex != null) {
            adjacencyList.putIfAbsent(vertex, new ArrayList<>());
        }
    }

    public void addEdge(Vertex source, Vertex destination, double weight) {
        if (source == null || destination == null) {
            return;
        }
        addVertex(source);
        addVertex(destination);
        
        Edge edge = new Edge(source, destination, weight);
        adjacencyList.get(source).add(edge);
    }

    public void addUndirectedEdge(Vertex source, Vertex destination, double weight) {
        addEdge(source, destination, weight);
        addEdge(destination, source, weight);
    }

    public List<Edge> getEdges(Vertex vertex) {
        return adjacencyList.getOrDefault(vertex, Collections.emptyList());
    }

    public Set<Vertex> getVertices() {
        return adjacencyList.keySet();
    }

    public Map<Vertex, List<Edge>> getAdjacencyList() {
        return adjacencyList;
    }

    public List<Vertex> bfs(Vertex start) {
        List<Vertex> visitedOrder = new ArrayList<>();
        if (start == null || !adjacencyList.containsKey(start)) {
            return visitedOrder;
        }

        Queue<Vertex> queue = new LinkedList<>();
        Set<Vertex> visited = new HashSet<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Vertex current = queue.poll();
            visitedOrder.add(current);

            List<Edge> edges = adjacencyList.getOrDefault(current, Collections.emptyList());
            for (Edge edge : edges) {
                Vertex neighbor = edge.getDestination();
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        return visitedOrder;
    }

    public List<Vertex> dfs(Vertex start) {
        List<Vertex> visitedOrder = new ArrayList<>();
        if (start == null || !adjacencyList.containsKey(start)) {
            return visitedOrder;
        }

        Set<Vertex> visited = new HashSet<>();
        dfsHelper(start, visited, visitedOrder);
        return visitedOrder;
    }

    private void dfsHelper(Vertex current, Set<Vertex> visited, List<Vertex> visitedOrder) {
        visited.add(current);
        visitedOrder.add(current);

        List<Edge> edges = adjacencyList.getOrDefault(current, Collections.emptyList());
        for (Edge edge : edges) {
            Vertex neighbor = edge.getDestination();
            if (!visited.contains(neighbor)) {
                dfsHelper(neighbor, visited, visitedOrder);
            }
        }
    }
}
