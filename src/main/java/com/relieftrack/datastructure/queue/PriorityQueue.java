package com.relieftrack.datastructure.queue;

public class PriorityQueue {

    // First node in the queue
    private PriorityQueueNode front;

    // Number of requests in the queue
    private int size;

    // Constructor
    public PriorityQueue() {
        this.front = null;
        this.size = 0;
    }

    // Check whether the queue is empty
    public boolean isEmpty() {
        return front == null;
    }

    // Return current queue size
    public int size() {
        return size;
    }

}
