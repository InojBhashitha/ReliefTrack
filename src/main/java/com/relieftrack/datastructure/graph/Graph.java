package com.relieftrack.datastructure.graph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;

/** Weighted, undirected graph with Dijkstra shortest-path routing. */
public class Graph<T> {
    private final Map<T, Map<T, Double>> adjacency = new HashMap<>();

    public void addVertex(T vertex) { adjacency.computeIfAbsent(vertex, ignored -> new HashMap<>()); }

    public void connect(T first, T second, double distance) {
        if (distance < 0) throw new IllegalArgumentException("Distance cannot be negative.");
        addVertex(first); addVertex(second);
        adjacency.get(first).put(second, distance);
        adjacency.get(second).put(first, distance);
    }

    public Optional<Path<T>> shortestPath(T source, T destination) {
        if (!adjacency.containsKey(source) || !adjacency.containsKey(destination)) return Optional.empty();
        Map<T, Double> distances = new HashMap<>();
        Map<T, T> previous = new HashMap<>();
        PriorityQueue<Route<T>> queue = new PriorityQueue<>(Comparator.comparingDouble(Route::distance));
        distances.put(source, 0.0); queue.add(new Route<>(source, 0.0));
        while (!queue.isEmpty()) {
            Route<T> current = queue.poll();
            if (current.distance() > distances.get(current.vertex())) continue;
            if (current.vertex().equals(destination)) return Optional.of(buildPath(source, destination, distances.get(destination), previous));
            for (Map.Entry<T, Double> edge : adjacency.get(current.vertex()).entrySet()) {
                double candidate = current.distance() + edge.getValue();
                if (candidate < distances.getOrDefault(edge.getKey(), Double.POSITIVE_INFINITY)) {
                    distances.put(edge.getKey(), candidate); previous.put(edge.getKey(), current.vertex());
                    queue.add(new Route<>(edge.getKey(), candidate));
                }
            }
        }
        return Optional.empty();
    }

    private Path<T> buildPath(T source, T destination, double distance, Map<T, T> previous) {
        List<T> vertices = new ArrayList<>();
        for (T current = destination; current != null; current = previous.get(current)) {
            vertices.add(0, current);
            if (current.equals(source)) break;
        }
        return new Path<>(vertices, distance);
    }

    public record Path<T>(List<T> vertices, double distance) { }
    private record Route<T>(T vertex, double distance) { }
}
