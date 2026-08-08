package com.relieftrack.model;

import java.time.LocalDateTime;

import com.relieftrack.enums.DispatchStatus;

public class Dispatch {

    private int dispatchId;
    private EmergencyRequest request;
    private Warehouse warehouse;
    private LocalDateTime dispatchDate;
    private DispatchStatus status;

    public Dispatch() {
    }

    public Dispatch(int dispatchId,
                    EmergencyRequest request,
                    Warehouse warehouse,
                    LocalDateTime dispatchDate,
                    DispatchStatus status) {
        this.dispatchId = dispatchId;
        this.request = request;
        this.warehouse = warehouse;
        this.dispatchDate = dispatchDate;
        this.status = status;
    }

    public int getDispatchId() {
        return dispatchId;
    }

    public void setDispatchId(int dispatchId) {
        this.dispatchId = dispatchId;
    }

    public EmergencyRequest getRequest() {
        return request;
    }

    public void setRequest(EmergencyRequest request) {
        this.request = request;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public LocalDateTime getDispatchDate() {
        return dispatchDate;
    }

    public void setDispatchDate(LocalDateTime dispatchDate) {
        this.dispatchDate = dispatchDate;
    }

    public DispatchStatus getStatus() {
        return status;
    }

    public void setStatus(DispatchStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ID: " + dispatchId + " | Warehouse: " + warehouse.getName() + " | Request ID: " + request.getRequestId() + " (Org: " + request.getOrganization() + ") | Status: " + status;
    }
}