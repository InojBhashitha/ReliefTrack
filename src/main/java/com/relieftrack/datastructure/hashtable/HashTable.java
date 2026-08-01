package com.relieftrack.datastructure.hashtable;

public class HashTable<K, V> {

    private static final int DEFAULT_CAPACITY = 16;
    private static final double DEFAULT_LOAD_FACTOR = 0.75;

    private HashEntry<K, V>[] buckets;
    private int size;
    private final double loadFactor;

    public HashTable() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public HashTable(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    @SuppressWarnings("unchecked")
    public HashTable(int initialCapacity, double loadFactor) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Initial capacity must be greater than 0.");
        }
        if (loadFactor <= 0 || loadFactor >= 1) {
            throw new IllegalArgumentException("Load factor must be between 0 and 1.");
        }

        this.buckets = new HashEntry[initialCapacity];
        this.loadFactor = loadFactor;
        this.size = 0;
    }

    public void put(K key, V value) {
        if (needsResize()) {
            resize();
        }

        int index = getIndex(key);
        HashEntry<K, V> current = buckets[index];

        while (current != null) {
            if (current.getKey() == null && key == null
                    || current.getKey() != null && current.getKey().equals(key)) {
                current.setValue(value);
                return;
            }
            current = current.getNext();
        }

        HashEntry<K, V> entry = new HashEntry<>(key, value);
        entry.setNext(buckets[index]);
        buckets[index] = entry;
        size++;
    }

    public V get(K key) {
        HashEntry<K, V> current = buckets[getIndex(key)];

        while (current != null) {
            if (current.getKey() == null && key == null
                    || current.getKey() != null && current.getKey().equals(key)) {
                return current.getValue();
            }
            current = current.getNext();
        }

        return null;
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    public V remove(K key) {
        int index = getIndex(key);
        HashEntry<K, V> current = buckets[index];
        HashEntry<K, V> previous = null;

        while (current != null) {
            if (current.getKey() == null && key == null
                    || current.getKey() != null && current.getKey().equals(key)) {
                V removedValue = current.getValue();

                if (previous == null) {
                    buckets[index] = current.getNext();
                } else {
                    previous.setNext(current.getNext());
                }

                size--;
                return removedValue;
            }

            previous = current;
            current = current.getNext();
        }

        return null;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = null;
        }
        size = 0;
    }

    private boolean needsResize() {
        return (double) size / buckets.length >= loadFactor;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        HashEntry<K, V>[] oldBuckets = buckets;
        buckets = new HashEntry[oldBuckets.length * 2];
        size = 0;

        for (HashEntry<K, V> bucket : oldBuckets) {
            while (bucket != null) {
                HashEntry<K, V> next = bucket.getNext();
                bucket.setNext(null);
                put(bucket.getKey(), bucket.getValue());
                bucket = next;
            }
        }
    }

    private int getIndex(K key) {
        int hash = key == null ? 0 : key.hashCode();
        return Math.floorMod(hash, buckets.length);
    }
}
