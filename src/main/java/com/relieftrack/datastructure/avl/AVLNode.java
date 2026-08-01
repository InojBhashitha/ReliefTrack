package com.relieftrack.datastructure.avl;

public class AVLNode<K extends Comparable<K>, V> {

    private K key;
    private V value;
    private int height = 1;
    private AVLNode<K, V> left;
    private AVLNode<K, V> right;

    AVLNode(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() { return key; }
    public V getValue() { return value; }
    void setValue(V value) { this.value = value; }
    int getHeight() { return height; }
    void setHeight(int height) { this.height = height; }
    AVLNode<K, V> getLeft() { return left; }
    void setLeft(AVLNode<K, V> left) { this.left = left; }
    AVLNode<K, V> getRight() { return right; }
    void setRight(AVLNode<K, V> right) { this.right = right; }
}
