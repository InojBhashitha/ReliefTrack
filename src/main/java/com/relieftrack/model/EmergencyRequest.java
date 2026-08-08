package com.relieftrack.model;

import java.time.LocalDateTime;

import com.relieftrack.enums.PriorityLevel;
import com.relieftrack.enums.RequestStatus;
import com.relieftrack.enums.DisasterType;

public class EmergencyRequest {

    private int requestId;
    private String organization;
    private DisasterType disasterType;
    private ReliefItem reliefItem;
    private int quantity;
    private PriorityLevel priority;
    private RequestStatus status;
    private LocalDateTime requestDate;

    public EmergencyRequest() {
    }

    public EmergencyRequest(int requestId,
                            String organization,
                            DisasterType disasterType,
                            ReliefItem reliefItem,
                            int quantity,
                            PriorityLevel priority,
                            RequestStatus status,
                            LocalDateTime requestDate) {
        this.requestId = requestId;
        this.organization = organization;
        this.disasterType = disasterType;
        this.reliefItem = reliefItem;
        this.quantity = quantity;
        this.priority = priority;
        this.status = status;
        this.requestDate = requestDate;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public DisasterType getDisasterType() {
        return disasterType;
    }

    public void setDisasterType(DisasterType disasterType) {
        this.disasterType = disasterType;
    }

    public ReliefItem getReliefItem() {
        return reliefItem;
    }

    public void setReliefItem(ReliefItem reliefItem) {
        this.reliefItem = reliefItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public PriorityLevel getPriority() {
        return priority;
    }

    public void setPriority(PriorityLevel priority) {
        this.priority = priority;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    @Override
    public String toString() {
        return organization + " | Disaster: " + disasterType + " | Item: " + reliefItem.getName() + " (Qty: " + quantity + ") | Priority: " + priority + " | Status: " + status;
    }
}