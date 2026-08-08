package com.relieftrack.datastructure.avl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** A self-balancing binary search tree for ordered in-memory lookups. */
public class AVLTree<K extends Comparable<K>, V> {

    private AVLNode<K, V> root;
    private int size;

    public void put(K key, V value) {
        if (key == null) throw new IllegalArgumentException("AVL tree keys cannot be null.");
        root = insert(root, key, value);
    }

    public Optional<V> get(K key) {
        if (key == null) return Optional.empty();
        AVLNode<K, V> node = root;
        while (node != null) {
            int comparison = key.compareTo(node.getKey());
            if (comparison == 0) return Optional.ofNullable(node.getValue());
            node = comparison < 0 ? node.getLeft() : node.getRight();
        }
        return Optional.empty();
    }

    public boolean containsKey(K key) { return get(key).isPresent(); }

    public Optional<V> remove(K key) {
        Optional<V> removed = get(key);
        if (removed.isPresent()) root = delete(root, key);
        return removed;
    }

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    public List<V> valuesInOrder() {
        List<V> values = new ArrayList<>();
        inOrder(root, values);
        return values;
    }

    public List<V> searchPrefix(String prefix) {
        List<V> results = new ArrayList<>();
        searchPrefix(root, prefix.toLowerCase().trim(), results);
        return results;
    }

    private void searchPrefix(AVLNode<K, V> node, String prefix, List<V> results) {
        if (node == null) return;
        String keyStr = node.getKey().toString().toLowerCase().trim();
        if (keyStr.startsWith(prefix)) {
            searchPrefix(node.getLeft(), prefix, results);
            results.add(node.getValue());
            searchPrefix(node.getRight(), prefix, results);
        } else {
            if (keyStr.compareTo(prefix) > 0) {
                searchPrefix(node.getLeft(), prefix, results);
            }
            if (keyStr.compareTo(prefix) < 0) {
                searchPrefix(node.getRight(), prefix, results);
            }
        }
    }

    private AVLNode<K, V> insert(AVLNode<K, V> node, K key, V value) {
        if (node == null) { size++; return new AVLNode<>(key, value); }
        int comparison = key.compareTo(node.getKey());
        if (comparison < 0) node.setLeft(insert(node.getLeft(), key, value));
        else if (comparison > 0) node.setRight(insert(node.getRight(), key, value));
        else { node.setValue(value); return node; }
        return rebalance(node);
    }

    private AVLNode<K, V> delete(AVLNode<K, V> node, K key) {
        int comparison = key.compareTo(node.getKey());
        if (comparison < 0) node.setLeft(delete(node.getLeft(), key));
        else if (comparison > 0) node.setRight(delete(node.getRight(), key));
        else {
            size--;
            if (node.getLeft() == null) return node.getRight();
            if (node.getRight() == null) return node.getLeft();
            AVLNode<K, V> successor = smallest(node.getRight());
            AVLNode<K, V> replacement = new AVLNode<>(successor.getKey(), successor.getValue());
            replacement.setLeft(node.getLeft());
            replacement.setRight(deleteWithoutSizeChange(node.getRight(), successor.getKey()));
            node = replacement;
        }
        return rebalance(node);
    }

    private AVLNode<K, V> deleteWithoutSizeChange(AVLNode<K, V> node, K key) {
        int comparison = key.compareTo(node.getKey());
        if (comparison < 0) node.setLeft(deleteWithoutSizeChange(node.getLeft(), key));
        else if (comparison > 0) node.setRight(deleteWithoutSizeChange(node.getRight(), key));
        else return node.getRight();
        return rebalance(node);
    }

    private AVLNode<K, V> smallest(AVLNode<K, V> node) {
        while (node.getLeft() != null) node = node.getLeft();
        return node;
    }

    private void inOrder(AVLNode<K, V> node, List<V> values) {
        if (node == null) return;
        inOrder(node.getLeft(), values);
        values.add(node.getValue());
        inOrder(node.getRight(), values);
    }

    private AVLNode<K, V> rebalance(AVLNode<K, V> node) {
        updateHeight(node);
        int balance = height(node.getLeft()) - height(node.getRight());
        if (balance > 1) {
            if (height(node.getLeft().getLeft()) < height(node.getLeft().getRight())) node.setLeft(rotateLeft(node.getLeft()));
            return rotateRight(node);
        }
        if (balance < -1) {
            if (height(node.getRight().getRight()) < height(node.getRight().getLeft())) node.setRight(rotateRight(node.getRight()));
            return rotateLeft(node);
        }
        return node;
    }

    private AVLNode<K, V> rotateLeft(AVLNode<K, V> node) {
        AVLNode<K, V> newRoot = node.getRight();
        node.setRight(newRoot.getLeft());
        newRoot.setLeft(node);
        updateHeight(node); updateHeight(newRoot);
        return newRoot;
    }

    private AVLNode<K, V> rotateRight(AVLNode<K, V> node) {
        AVLNode<K, V> newRoot = node.getLeft();
        node.setLeft(newRoot.getRight());
        newRoot.setRight(node);
        updateHeight(node); updateHeight(newRoot);
        return newRoot;
    }

    private int height(AVLNode<K, V> node) { return node == null ? 0 : node.getHeight(); }
    private void updateHeight(AVLNode<K, V> node) { node.setHeight(1 + Math.max(height(node.getLeft()), height(node.getRight()))); }
}
