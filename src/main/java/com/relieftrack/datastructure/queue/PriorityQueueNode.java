package com.relieftrack.datastructure.queue;

import com.relieftrack.model.EmergencyRequest;

public class PriorityQueueNode {

    private EmergencyRequest request;
    private PriorityQueueNode next;

    //Constructor
    public PriorityQueueNode(EmergencyRequest request){

        this.request = request;
        this.next = null;
    }

    public EmergencyRequest getRequest(){

        return request;
    }

    public void setRequest(EmergencyRequest request){

        this.request = request;
    }

    public PriorityQueueNode getNext() {

        return next;
    }

    public void setNext(PriorityQueueNode next){

        this.next = next;
    }

}
