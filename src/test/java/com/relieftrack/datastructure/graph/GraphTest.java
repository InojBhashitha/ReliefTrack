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

    @Test
    public void testBFSAndDFS() {
        Graph graph = new Graph();
        Vertex vA = new Vertex("A", "Location A");
        Vertex vB = new Vertex("B", "Location B");
        Vertex vC = new Vertex("C", "Location C");
        Vertex vD = new Vertex("D", "Location D");

        // Set up a simple graph: A-B, A-C, B-D
        graph.addUndirectedEdge(vA, vB, 1.0);
        graph.addUndirectedEdge(vA, vC, 1.0);
        graph.addUndirectedEdge(vB, vD, 1.0);

        // BFS traversal from A
        List<Vertex> bfsOrder = graph.bfs(vA);
        assertEquals(4, bfsOrder.size());
        assertEquals(vA, bfsOrder.get(0));
        // Neighbors of A (B and C) should come before D
        assertTrue(bfsOrder.indexOf(vB) < bfsOrder.indexOf(vD));
        assertTrue(bfsOrder.indexOf(vC) < bfsOrder.indexOf(vD));

        // DFS traversal from A
        List<Vertex> dfsOrder = graph.dfs(vA);
        assertEquals(4, dfsOrder.size());
        assertEquals(vA, dfsOrder.get(0));
    }

    @Test
    public void testDijkstraShortestPath() {
        Graph graph = new Graph();
        Vertex colombo = new Vertex("COL", "Colombo");
        Vertex kandy = new Vertex("KAN", "Kandy");
        Vertex galle = new Vertex("GAL", "Galle");
        Vertex matara = new Vertex("MAT", "Matara");

        // Set up distances
        // Colombo to Kandy: 115 km
        // Colombo to Galle: 125 km
        // Galle to Matara: 45 km
        // Kandy to Matara: 200 km
        graph.addUndirectedEdge(colombo, kandy, 115.0);
        graph.addUndirectedEdge(colombo, galle, 125.0);
        graph.addUndirectedEdge(galle, matara, 45.0);
        graph.addUndirectedEdge(kandy, matara, 200.0);

        // Find shortest distances from Colombo
        java.util.Map<Vertex, Double> shortestDistances = graph.dijkstra(colombo);

        // Verify shortest path distances
        assertEquals(0.0, shortestDistances.get(colombo));
        assertEquals(115.0, shortestDistances.get(kandy));
        assertEquals(125.0, shortestDistances.get(galle));
        // Colombo -> Galle (125) -> Matara (45) = 170 km, which is shorter than Colombo -> Kandy (115) -> Matara (200) = 315 km
        assertEquals(170.0, shortestDistances.get(matara));
    }

    @Test
    public void testGetShortestPath() {
        Graph graph = new Graph();
        Vertex colombo = new Vertex("COL", "Colombo");
        Vertex kandy = new Vertex("KAN", "Kandy");
        Vertex galle = new Vertex("GAL", "Galle");
        Vertex matara = new Vertex("MAT", "Matara");

        graph.addUndirectedEdge(colombo, kandy, 115.0);
        graph.addUndirectedEdge(colombo, galle, 125.0);
        graph.addUndirectedEdge(galle, matara, 45.0);
        graph.addUndirectedEdge(kandy, matara, 200.0);

        List<Vertex> path = graph.getShortestPath(colombo, matara);

        assertEquals(3, path.size());
        assertEquals(colombo, path.get(0));
        assertEquals(galle, path.get(1));
        assertEquals(matara, path.get(2));

        // Test unreachable destination
        Vertex jaffna = new Vertex("JAF", "Jaffna");
        graph.addVertex(jaffna);
        List<Vertex> unreachablePath = graph.getShortestPath(colombo, jaffna);
        assertTrue(unreachablePath.isEmpty());
    }

    @Test
    public void testNegativeEdgeWeightRejected() {
        Graph graph = new Graph();
        Vertex v1 = new Vertex("W1", "Colombo");
        Vertex v2 = new Vertex("W2", "Kandy");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            graph.addEdge(v1, v2, -5.0);
        });
        assertTrue(exception.getMessage().contains("negative"));
        assertTrue(graph.getEdges(v1).isEmpty());
    }

    @Test
    public void testDuplicateEdgeRejected() {
        Graph graph = new Graph();
        Vertex v1 = new Vertex("W1", "Colombo");
        Vertex v2 = new Vertex("W2", "Kandy");

        graph.addEdge(v1, v2, 10.0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            graph.addEdge(v1, v2, 15.0);
        });
        assertTrue(exception.getMessage().contains("already exists"));
        assertEquals(1, graph.getEdges(v1).size());
    }

    @Test
    public void testGetVerticesIsUnmodifiable() {
        Graph graph = new Graph();
        Vertex v1 = new Vertex("W1", "Colombo");
        graph.addVertex(v1);

        assertThrows(UnsupportedOperationException.class, () -> {
            graph.getVertices().add(new Vertex("W2", "Kandy"));
        });
        assertThrows(UnsupportedOperationException.class, () -> {
            graph.getVertices().remove(v1);
        });
    }

    @Test
    public void testGetAdjacencyListIsUnmodifiable() {
        Graph graph = new Graph();
        Vertex v1 = new Vertex("W1", "Colombo");
        Vertex v2 = new Vertex("W2", "Kandy");
        graph.addEdge(v1, v2, 10.0);

        // Cannot modify the map
        assertThrows(UnsupportedOperationException.class, () -> {
            graph.getAdjacencyList().remove(v1);
        });
        assertThrows(UnsupportedOperationException.class, () -> {
            graph.getAdjacencyList().put(new Vertex("W3", "Galle"), List.of());
        });

        // Cannot modify the lists inside the map
        assertThrows(UnsupportedOperationException.class, () -> {
            graph.getAdjacencyList().get(v1).add(new Edge(v1, v2, 20.0));
        });
    }

    @Test
    public void testGetEdgesIsUnmodifiable() {
        Graph graph = new Graph();
        Vertex v1 = new Vertex("W1", "Colombo");
        Vertex v2 = new Vertex("W2", "Kandy");
        graph.addEdge(v1, v2, 10.0);

        assertThrows(UnsupportedOperationException.class, () -> {
            graph.getEdges(v1).add(new Edge(v1, v2, 20.0));
        });
    }

    @Test
    public void testVertexIdIsAssignedDuringConstruction() {
        Vertex v = new Vertex("V1", "Location A");
        assertEquals("V1", v.getId());
        assertEquals("Location A", v.getName());
    }

    @Test
    public void testVertexIdFieldIsFinal() throws NoSuchFieldException {
        java.lang.reflect.Field idField = Vertex.class.getDeclaredField("id");
        assertTrue(java.lang.reflect.Modifier.isFinal(idField.getModifiers()),
                "Vertex.id field should be declared final.");
    }

    @Test
    public void testVertexHasNoSetIdMethod() {
        try {
            Vertex.class.getMethod("setId", String.class);
            fail("Vertex should not have a public setId method.");
        } catch (NoSuchMethodException expected) {
            // expected — setId should not exist
        }
    }

    @Test
    public void testVertexNameRemainsModifiable() {
        Vertex v = new Vertex("V1", "Old Name");
        v.setName("New Name");
        assertEquals("New Name", v.getName());
        assertEquals("V1", v.getId());
    }

    @Test
    public void testGraphLookupWorksWithImmutableVertexId() {
        Graph graph = new Graph();
        Vertex v1 = new Vertex("W1", "Colombo");
        Vertex v2 = new Vertex("W2", "Kandy");
        graph.addEdge(v1, v2, 100.0);

        // Lookup by equal vertex object
        Vertex lookup = new Vertex("W1", "Colombo");
        List<Edge> edges = graph.getEdges(lookup);
        assertEquals(1, edges.size());
        assertEquals(v2, edges.get(0).getDestination());

        // Verify graph contains both vertices
        assertTrue(graph.getVertices().contains(v1));
        assertTrue(graph.getVertices().contains(lookup));
    }
}

