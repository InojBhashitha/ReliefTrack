package com.relieftrack.service;

import com.relieftrack.datastructure.queue.PriorityQueue;
import com.relieftrack.model.EmergencyRequest;
import com.relieftrack.repository.EmergencyRequestRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class EmergencyRequestService {

    private final EmergencyRequestRepository emergencyRequestRepository;

    public EmergencyRequestService() {
        this.emergencyRequestRepository = new EmergencyRequestRepository();
    }

    public void save(EmergencyRequest request) throws SQLException {
        emergencyRequestRepository.save(request);
    }

    public void update(EmergencyRequest request) throws SQLException {
        emergencyRequestRepository.update(request);
    }

    public void delete(int id) throws SQLException {
        emergencyRequestRepository.delete(id);
    }

    public EmergencyRequest findById(int id) throws SQLException {
        return emergencyRequestRepository.findById(id);
    }

    public List<EmergencyRequest> findAll() throws SQLException {
        return emergencyRequestRepository.findAll();
    }

    /** Returns pending requests in the order they should be handled. */
    public List<EmergencyRequest> prioritizePendingRequests(List<EmergencyRequest> requests) {
        PriorityQueue queue = new PriorityQueue();
        List<EmergencyRequest> ordered = new ArrayList<>();
        for (EmergencyRequest request : requests) {
            if (request.getStatus() == com.relieftrack.enums.RequestStatus.PENDING) {
                queue.enqueue(request);
            }
        }
        while (!queue.isEmpty()) {
            ordered.add(queue.dequeue());
        }
        return ordered;
    }
}
