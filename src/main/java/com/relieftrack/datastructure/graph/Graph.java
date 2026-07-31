package com.relieftrack.datastructure.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
}
