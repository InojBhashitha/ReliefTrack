package com.relieftrack.service;

import com.relieftrack.model.EmergencyRequest;
import com.relieftrack.repository.EmergencyRequestRepository;

import java.sql.SQLException;
import java.util.List;

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
}
