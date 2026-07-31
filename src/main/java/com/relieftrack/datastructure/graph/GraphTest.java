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

        // Since it's directed, v2 should not have outgoing edges to v1 automatically
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

    @Test
    public void testBfsTraversal() {
        Graph graph = new Graph();
        Vertex v1 = new Vertex("W1", "Colombo");
        Vertex v2 = new Vertex("W2", "Kandy");
        Vertex v3 = new Vertex("W3", "Galle");
        Vertex v4 = new Vertex("W4", "Jaffna");

        // Set up a simple graph structure
        // W1 -> W2, W1 -> W3
        // W2 -> W4
        graph.addEdge(v1, v2, 115.0);
        graph.addEdge(v1, v3, 120.0);
        graph.addEdge(v2, v4, 320.0);

        List<Vertex> bfsResult = graph.bfs(v1);

        assertEquals(4, bfsResult.size());
        assertEquals(v1, bfsResult.get(0));
        // BFS order should explore immediate neighbors (v2, v3) before going deeper (v4)
        assertTrue(bfsResult.get(1).equals(v2) || bfsResult.get(1).equals(v3));
        assertTrue(bfsResult.get(2).equals(v2) || bfsResult.get(2).equals(v3));
        assertEquals(v4, bfsResult.get(3));
    }
}
