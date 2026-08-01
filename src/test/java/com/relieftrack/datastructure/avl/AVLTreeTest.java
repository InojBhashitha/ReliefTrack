package com.relieftrack.datastructure.avl;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AVLTreeTest {

    @Test
    void storesValuesInSortedKeyOrderAndReplacesExistingValues() {
        AVLTree<Integer, String> tree = new AVLTree<>();
        tree.put(30, "thirty");
        tree.put(10, "ten");
        tree.put(20, "twenty");
        tree.put(10, "updated");

        assertEquals(3, tree.size());
        assertEquals("updated", tree.get(10).orElseThrow());
        assertEquals(List.of("updated", "twenty", "thirty"), tree.valuesInOrder());
    }

    @Test
    void removesNodesIncludingNodesWithTwoChildren() {
        AVLTree<Integer, Integer> tree = new AVLTree<>();
        for (int key : List.of(30, 20, 40, 10, 25, 35, 50)) tree.put(key, key);

        assertEquals(30, tree.remove(30).orElseThrow());
        assertFalse(tree.containsKey(30));
        assertEquals(List.of(10, 20, 25, 35, 40, 50), tree.valuesInOrder());
        assertEquals(6, tree.size());
    }
}
