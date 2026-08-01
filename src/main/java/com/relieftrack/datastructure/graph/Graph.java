package com.relieftrack.datastructure.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
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

    public Map<Vertex, Double> dijkstra(Vertex source) {
        Map<Vertex, Double> distances = new HashMap<>();
        if (source == null || !adjacencyList.containsKey(source)) {
            return distances;
        }

        // Initialize distances
        for (Vertex v : adjacencyList.keySet()) {
            distances.put(v, Double.MAX_VALUE);
        }
        distances.put(source, 0.0);

        // Helper class to store vertex-distance pairs in PriorityQueue
        class NodeDistance implements Comparable<NodeDistance> {
            final Vertex vertex;
            final double distance;

            NodeDistance(Vertex vertex, double distance) {
                this.vertex = vertex;
                this.distance = distance;
            }

            @Override
            public int compareTo(NodeDistance o) {
                return Double.compare(this.distance, o.distance);
            }
        }

        PriorityQueue<NodeDistance> pq = new PriorityQueue<>();
        pq.add(new NodeDistance(source, 0.0));
        Set<Vertex> settled = new HashSet<>();

        while (!pq.isEmpty()) {
            NodeDistance current = pq.poll();
            Vertex u = current.vertex;

            if (settled.contains(u)) {
                continue;
            }
            settled.add(u);

            List<Edge> edges = adjacencyList.getOrDefault(u, Collections.emptyList());
            for (Edge edge : edges) {
                Vertex v = edge.getDestination();
                if (!settled.contains(v)) {
                    double newDist = distances.get(u) + edge.getWeight();
                    if (newDist < distances.get(v)) {
                        distances.put(v, newDist);
                        pq.add(new NodeDistance(v, newDist));
                    }
                }
            }
        }

        return distances;
    }

    public List<Vertex> getShortestPath(Vertex source, Vertex destination) {
        if (source == null || destination == null || !adjacencyList.containsKey(source) || !adjacencyList.containsKey(destination)) {
            return Collections.emptyList();
        }

        Map<Vertex, Double> distances = new HashMap<>();
        Map<Vertex, Vertex> predecessors = new HashMap<>();

        for (Vertex v : adjacencyList.keySet()) {
            distances.put(v, Double.MAX_VALUE);
        }
        distances.put(source, 0.0);

        class NodeDistance implements Comparable<NodeDistance> {
            final Vertex vertex;
            final double distance;

            NodeDistance(Vertex vertex, double distance) {
                this.vertex = vertex;
                this.distance = distance;
            }

            @Override
            public int compareTo(NodeDistance o) {
                return Double.compare(this.distance, o.distance);
            }
        }

        PriorityQueue<NodeDistance> pq = new PriorityQueue<>();
        pq.add(new NodeDistance(source, 0.0));
        Set<Vertex> settled = new HashSet<>();

        while (!pq.isEmpty()) {
            NodeDistance current = pq.poll();
            Vertex u = current.vertex;

            if (u.equals(destination)) {
                break;
            }

            if (settled.contains(u)) {
                continue;
            }
            settled.add(u);

            List<Edge> edges = adjacencyList.getOrDefault(u, Collections.emptyList());
            for (Edge edge : edges) {
                Vertex v = edge.getDestination();
                if (!settled.contains(v)) {
                    double newDist = distances.get(u) + edge.getWeight();
                    if (newDist < distances.get(v)) {
                        distances.put(v, newDist);
                        predecessors.put(v, u);
                        pq.add(new NodeDistance(v, newDist));
                    }
                }
            }
        }

        if (distances.get(destination) == Double.MAX_VALUE) {
            return Collections.emptyList();
        }

        List<Vertex> path = new ArrayList<>();
        Vertex step = destination;
        while (step != null) {
            path.add(step);
            step = predecessors.get(step);
        }
        Collections.reverse(path);
        return path;
    }
}
