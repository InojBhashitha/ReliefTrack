package com.relieftrack.datastructure.graph;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class GraphTest {

    @Test
    public void testAddVertex() {
        Graph graph = new Graph();
        Vertex v1 = new Vertex("W1", "Colombo Warehouse");
        Vertex v2 = new Vertex("W2", "Kandy Warehouse");

        graph.addVertex(v1);
        graph.addVertex(v2);

        assertTrue(graph.getVertices().contains(v1));
        assertTrue(graph.getVertices().contains(v2));
        assertEquals(2, graph.getVertices().size());
    }

    @Test
    public void testAddEdgeDirected() {
        Graph graph = new Graph();
        Vertex v1 = new Vertex("W1", "Colombo Warehouse");
        Vertex v2 = new Vertex("W2", "Kandy Warehouse");

        graph.addEdge(v1, v2, 115.5);

        List<Edge> edgesFromV1 = graph.getEdges(v1);
        assertEquals(1, edgesFromV1.size());
        assertEquals(v1, edgesFromV1.get(0).getSource());
        assertEquals(v2, edgesFromV1.get(0).getDestination());
        assertEquals(115.5, edgesFromV1.get(0).getWeight());

        List<Edge> edgesFromV2 = graph.getEdges(v2);
        assertTrue(edgesFromV2.isEmpty());
    }

    @Test
    public void testAddEdgeUndirected() {
        Graph graph = new Graph();
        Vertex v1 = new Vertex("W1", "Colombo Warehouse");
        Vertex v2 = new Vertex("W2", "Kandy Warehouse");

        graph.addUndirectedEdge(v1, v2, 115.5);

        List<Edge> edgesFromV1 = graph.getEdges(v1);
        assertEquals(1, edgesFromV1.size());
        assertEquals(v2, edgesFromV1.get(0).getDestination());

        List<Edge> edgesFromV2 = graph.getEdges(v2);
        assertEquals(1, edgesFromV2.size());
        assertEquals(v1, edgesFromV2.get(0).getDestination());
    }
}
