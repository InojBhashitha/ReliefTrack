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

    private int getPriorityValue(PriorityLevel priority) {
        switch (priority) {
            case CRITICAL:
                return 4;
            case HIGH:
                return 3;
            case MEDIUM:
                return 2;
            case LOW:
                return 1;
            default:
                return 0;
        }
    }

    public void enqueue(EmergencyRequest request) {

        PriorityQueueNode newNode = new PriorityQueueNode(request);

        // Case 1 - Queue is empty
        if (isEmpty()) {
            front = newNode;
            size++;
            return;
        }

        // Case 2 - Insert at the front
        if (getPriorityValue(request.getPriority()) > getPriorityValue(front.getRequest().getPriority())) {
            newNode.setNext(front);
            front = newNode;
            size++;
            return;
        }

        PriorityQueueNode current = front;

        while (current.getNext() != null && getPriorityValue(
                current.getNext().getRequest().getPriority()) >= getPriorityValue(request.getPriority())) {
            current = current.getNext();
        }

        newNode.setNext(current.getNext());
        current.setNext(newNode);
        size++;

    }

    public EmergencyRequest dequeue() {
        if (isEmpty()) {
            return null;
        }

        EmergencyRequest request = front.getRequest();
        front = front.getNext();
        size--;

        return request;
    }

    public EmergencyRequest peek() {
        if (isEmpty()) {
            return null;
        }

        return front.getRequest();
    }

    public void display() {

        if (isEmpty()) {
            System.out.println("Priority Queue is empty.");
            return;
        }

        PriorityQueueNode current = front;

        System.out.println("------ Priority Queue ------");

        while (current != null) {

            System.out.println(current.getRequest());

            current = current.getNext();
        }

        System.out.println("----------------------------");
    }
}
