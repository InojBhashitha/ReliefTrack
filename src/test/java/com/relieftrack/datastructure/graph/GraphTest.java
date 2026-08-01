package com.relieftrack.datastructure.graph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {

    @Test
    void findsTheShortestWeightedRoute() {
        Graph<String> graph = new Graph<>();
        graph.connect("Central", "North", 8);
        graph.connect("Central", "Coastal", 3);
        graph.connect("Coastal", "North", 2);
        graph.connect("North", "Remote", 4);
        graph.connect("Coastal", "Remote", 10);

        Graph.Path<String> route = graph.shortestPath("Central", "Remote").orElseThrow();

        assertEquals(9, route.distance());
        assertEquals(java.util.List.of("Central", "Coastal", "North", "Remote"), route.vertices());
    }

    @Test
    void returnsEmptyForUnreachableDestinations() {
        Graph<String> graph = new Graph<>();
        graph.connect("A", "B", 1);
        graph.addVertex("C");

        assertTrue(graph.shortestPath("A", "C").isEmpty());
    }
}
