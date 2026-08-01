package com.relieftrack.datastructure.hashtable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashTableTest {

    @Test
    void putAndGetShouldWork() {
        HashTable<String, Integer> table = new HashTable<>();

        table.put("alice", 10);
        table.put("bob", 20);

        assertEquals(10, table.get("alice"));
        assertEquals(20, table.get("bob"));
        assertTrue(table.containsKey("alice"));
        assertEquals(2, table.size());
    }

    @Test
    void removeShouldReturnRemovedValue() {
        HashTable<String, Integer> table = new HashTable<>();

        table.put("alice", 10);
        Integer removed = table.remove("alice");

        assertEquals(10, removed);
        assertFalse(table.containsKey("alice"));
        assertTrue(table.isEmpty());
    }
}
