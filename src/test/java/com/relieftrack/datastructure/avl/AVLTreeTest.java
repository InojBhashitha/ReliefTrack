package com.relieftrack.datastructure.avl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AVLTreeTest {

    private AVLTree<Integer, String> tree;

    @BeforeEach
    void setUp() {
        tree = new AVLTree<>();
    }

    @Test
    void testLeftLeftRotation() {
        // Inserting 30, 20, 10 causes LL imbalance -> Right rotation at 30
        tree.put(30, "thirty");
        tree.put(20, "twenty");
        tree.put(10, "ten");

        assertTrue(tree.isBalanced());
        assertEquals(20, tree.getRoot().getKey());
        assertEquals(10, tree.getRoot().getLeft().getKey());
        assertEquals(30, tree.getRoot().getRight().getKey());
        assertEquals(List.of("ten", "twenty", "thirty"), tree.valuesInOrder());
    }

    @Test
    void testRightRightRotation() {
        // Inserting 10, 20, 30 causes RR imbalance -> Left rotation at 10
        tree.put(10, "ten");
        tree.put(20, "twenty");
        tree.put(30, "thirty");

        assertTrue(tree.isBalanced());
        assertEquals(20, tree.getRoot().getKey());
        assertEquals(10, tree.getRoot().getLeft().getKey());
        assertEquals(30, tree.getRoot().getRight().getKey());
        assertEquals(List.of("ten", "twenty", "thirty"), tree.valuesInOrder());
    }

    @Test
    void testLeftRightRotation() {
        // Inserting 30, 10, 20 causes LR imbalance -> Left rotate 10, Right rotate 30
        tree.put(30, "thirty");
        tree.put(10, "ten");
        tree.put(20, "twenty");

        assertTrue(tree.isBalanced());
        assertEquals(20, tree.getRoot().getKey());
        assertEquals(10, tree.getRoot().getLeft().getKey());
        assertEquals(30, tree.getRoot().getRight().getKey());
    }

    @Test
    void testRightLeftRotation() {
        // Inserting 10, 30, 20 causes RL imbalance -> Right rotate 30, Left rotate 10
        tree.put(10, "ten");
        tree.put(30, "thirty");
        tree.put(20, "twenty");

        assertTrue(tree.isBalanced());
        assertEquals(20, tree.getRoot().getKey());
        assertEquals(10, tree.getRoot().getLeft().getKey());
        assertEquals(30, tree.getRoot().getRight().getKey());
    }

    @Test
    void storesValuesInSortedKeyOrderAndReplacesExistingValues() {
        tree.put(30, "thirty");
        tree.put(10, "ten");
        tree.put(20, "twenty");
        tree.put(10, "updated");

        assertEquals(3, tree.size());
        assertEquals("updated", tree.get(10).orElseThrow());
        assertEquals(List.of("updated", "twenty", "thirty"), tree.valuesInOrder());
        assertTrue(tree.isBalanced());
    }

    @Test
    void removesNodesIncludingNodesWithTwoChildren() {
        AVLTree<Integer, Integer> intTree = new AVLTree<>();
        for (int key : List.of(30, 20, 40, 10, 25, 35, 50)) {
            intTree.put(key, key);
        }

        assertEquals(30, intTree.remove(30).orElseThrow());
        assertFalse(intTree.containsKey(30));
        assertEquals(List.of(10, 20, 25, 35, 40, 50), intTree.valuesInOrder());
        assertEquals(6, intTree.size());
        assertTrue(intTree.isBalanced());
    }

    @Test
    void testTraversals() {
        tree.put(20, "20");
        tree.put(10, "10");
        tree.put(30, "30");

        assertEquals(List.of("10", "20", "30"), tree.valuesInOrder());
        assertEquals(List.of("20", "10", "30"), tree.valuesPreOrder());
        assertEquals(List.of("10", "30", "20"), tree.valuesPostOrder());
    }

    @Test
    void testMinMaxKeysAndRangeSearch() {
        tree.put(15, "15");
        tree.put(10, "10");
        tree.put(25, "25");
        tree.put(5, "5");
        tree.put(20, "20");
        tree.put(30, "30");

        assertEquals(5, tree.getMinKey().orElseThrow());
        assertEquals(30, tree.getMaxKey().orElseThrow());

        List<String> rangeResult = tree.rangeSearch(10, 25);
        assertEquals(List.of("10", "15", "20", "25"), rangeResult);
    }

    @Test
    void testClearAndEmptyTree() {
        tree.put(1, "one");
        tree.put(2, "two");
        assertFalse(tree.isEmpty());

        tree.clear();
        assertTrue(tree.isEmpty());
        assertEquals(0, tree.size());
        assertTrue(tree.getMinKey().isEmpty());
        assertTrue(tree.getMaxKey().isEmpty());
        assertTrue(tree.isBalanced());
    }
}
