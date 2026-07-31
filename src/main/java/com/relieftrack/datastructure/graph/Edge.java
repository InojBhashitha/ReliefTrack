package com.relieftrack.datastructure.graph;

public class Edge {
    private Vertex source;
    private Vertex destination;
    private double weight;

    public Edge(Vertex source, Vertex destination, double weight) {
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }
}
