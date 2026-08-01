package com.relieftrack.datastructure.avl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A self-balancing binary search tree (AVL Tree) for ordered in-memory lookups,
 * insertions, deletions, traversals, and range searches in O(log n) time complexity.
 *
 * @param <K> the type of keys maintained by this tree (must implement Comparable)
 * @param <V> the type of mapped values
 */
public class AVLTree<K extends Comparable<K>, V> {

    private AVLNode<K, V> root;
    private int size;

    public AVLNode<K, V> getRoot() {
        return root;
    }

    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("AVL tree keys cannot be null.");
        }
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

    public boolean containsKey(K key) {
        return get(key).isPresent();
    }

    public Optional<V> remove(K key) {
        Optional<V> removed = get(key);
        if (removed.isPresent()) {
            root = delete(root, key);
        }
        return removed;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        root = null;
        size = 0;
    }

    public Optional<K> getMinKey() {
        if (root == null) return Optional.empty();
        return Optional.of(smallest(root).getKey());
    }

    public Optional<K> getMaxKey() {
        if (root == null) return Optional.empty();
        AVLNode<K, V> current = root;
        while (current.getRight() != null) {
            current = current.getRight();
        }
        return Optional.of(current.getKey());
    }

    public List<V> valuesInOrder() {
        List<V> values = new ArrayList<>();
        inOrder(root, values);
        return values;
    }

    public List<V> valuesPreOrder() {
        List<V> values = new ArrayList<>();
        preOrder(root, values);
        return values;
    }

    public List<V> valuesPostOrder() {
        List<V> values = new ArrayList<>();
        postOrder(root, values);
        return values;
    }

    public List<V> rangeSearch(K minKey, K maxKey) {
        List<V> result = new ArrayList<>();
        rangeSearchHelper(root, minKey, maxKey, result);
        return result;
    }

    public int getBalanceFactor(AVLNode<K, V> node) {
        if (node == null) return 0;
        return height(node.getLeft()) - height(node.getRight());
    }

    public boolean isBalanced() {
        return checkBalance(root);
    }

    private boolean checkBalance(AVLNode<K, V> node) {
        if (node == null) return true;
        int bf = getBalanceFactor(node);
        if (Math.abs(bf) > 1) return false;
        return checkBalance(node.getLeft()) && checkBalance(node.getRight());
    }

    private AVLNode<K, V> insert(AVLNode<K, V> node, K key, V value) {
        if (node == null) {
            size++;
            return new AVLNode<>(key, value);
        }
        int comparison = key.compareTo(node.getKey());
        if (comparison < 0) {
            node.setLeft(insert(node.getLeft(), key, value));
        } else if (comparison > 0) {
            node.setRight(insert(node.getRight(), key, value));
        } else {
            node.setValue(value);
            return node;
        }
        return rebalance(node);
    }

    private AVLNode<K, V> delete(AVLNode<K, V> node, K key) {
        if (node == null) return null;

        int comparison = key.compareTo(node.getKey());
        if (comparison < 0) {
            node.setLeft(delete(node.getLeft(), key));
        } else if (comparison > 0) {
            node.setRight(delete(node.getRight(), key));
        } else {
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
        if (node == null) return null;

        int comparison = key.compareTo(node.getKey());
        if (comparison < 0) {
            node.setLeft(deleteWithoutSizeChange(node.getLeft(), key));
        } else if (comparison > 0) {
            node.setRight(deleteWithoutSizeChange(node.getRight(), key));
        } else {
            return node.getRight();
        }
        return rebalance(node);
    }

    private AVLNode<K, V> smallest(AVLNode<K, V> node) {
        while (node.getLeft() != null) {
            node = node.getLeft();
        }
        return node;
    }

    private void inOrder(AVLNode<K, V> node, List<V> values) {
        if (node == null) return;
        inOrder(node.getLeft(), values);
        values.add(node.getValue());
        inOrder(node.getRight(), values);
    }

    private void preOrder(AVLNode<K, V> node, List<V> values) {
        if (node == null) return;
        values.add(node.getValue());
        preOrder(node.getLeft(), values);
        preOrder(node.getRight(), values);
    }

    private void postOrder(AVLNode<K, V> node, List<V> values) {
        if (node == null) return;
        postOrder(node.getLeft(), values);
        postOrder(node.getRight(), values);
        values.add(node.getValue());
    }

    private void rangeSearchHelper(AVLNode<K, V> node, K minKey, K maxKey, List<V> result) {
        if (node == null) return;

        if (minKey != null && minKey.compareTo(node.getKey()) < 0) {
            rangeSearchHelper(node.getLeft(), minKey, maxKey, result);
        }

        boolean withinMin = minKey == null || minKey.compareTo(node.getKey()) <= 0;
        boolean withinMax = maxKey == null || maxKey.compareTo(node.getKey()) >= 0;
        if (withinMin && withinMax) {
            result.add(node.getValue());
        }

        if (maxKey != null && maxKey.compareTo(node.getKey()) > 0) {
            rangeSearchHelper(node.getRight(), minKey, maxKey, result);
        }
    }

    private AVLNode<K, V> rebalance(AVLNode<K, V> node) {
        updateHeight(node);
        int balance = height(node.getLeft()) - height(node.getRight());
        if (balance > 1) {
            if (height(node.getLeft().getLeft()) < height(node.getLeft().getRight())) {
                node.setLeft(rotateLeft(node.getLeft()));
            }
            return rotateRight(node);
        }
        if (balance < -1) {
            if (height(node.getRight().getRight()) < height(node.getRight().getLeft())) {
                node.setRight(rotateRight(node.getRight()));
            }
            return rotateLeft(node);
        }
        return node;
    }

    private AVLNode<K, V> rotateLeft(AVLNode<K, V> node) {
        AVLNode<K, V> newRoot = node.getRight();
        node.setRight(newRoot.getLeft());
        newRoot.setLeft(node);
        updateHeight(node);
        updateHeight(newRoot);
        return newRoot;
    }

    private AVLNode<K, V> rotateRight(AVLNode<K, V> node) {
        AVLNode<K, V> newRoot = node.getLeft();
        node.setLeft(newRoot.getRight());
        newRoot.setRight(node);
        updateHeight(node);
        updateHeight(newRoot);
        return newRoot;
    }

    private int height(AVLNode<K, V> node) {
        return node == null ? 0 : node.getHeight();
    }

    private void updateHeight(AVLNode<K, V> node) {
        node.setHeight(1 + Math.max(height(node.getLeft()), height(node.getRight())));
    }
}
