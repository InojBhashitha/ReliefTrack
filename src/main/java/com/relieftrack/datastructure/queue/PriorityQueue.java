package com.relieftrack.datastructure.queue;

import com.relieftrack.enums.PriorityLevel;
import com.relieftrack.model.EmergencyRequest;

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

    /** Adds a request in descending priority order (CRITICAL first). */
    public void enqueue(EmergencyRequest request) {
        if (request == null || request.getPriority() == null) {
            throw new IllegalArgumentException("A request with a priority is required.");
        }
        PriorityQueueNode newNode = new PriorityQueueNode(request);
        if (front == null || hasHigherPriority(request, front.getRequest())) {
            newNode.setNext(front);
            front = newNode;
        } else {
            PriorityQueueNode current = front;
            while (current.getNext() != null && !hasHigherPriority(request, current.getNext().getRequest())) {
                current = current.getNext();
            }
            newNode.setNext(current.getNext());
            current.setNext(newNode);
        }
        size++;
    }

    public EmergencyRequest dequeue() {
        if (front == null) return null;
        EmergencyRequest request = front.getRequest();
        front = front.getNext();
        size--;
        return request;
    }

    public EmergencyRequest peek() { return front == null ? null : front.getRequest(); }

    private boolean hasHigherPriority(EmergencyRequest first, EmergencyRequest second) {
        return first.getPriority().ordinal() > second.getPriority().ordinal();
    }

}
