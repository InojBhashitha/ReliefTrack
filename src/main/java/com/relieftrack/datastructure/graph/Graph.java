package com.relieftrack.datastructure.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
