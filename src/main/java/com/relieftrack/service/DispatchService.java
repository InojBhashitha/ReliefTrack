package com.relieftrack.service;

import com.relieftrack.model.Dispatch;
import com.relieftrack.repository.DispatchRepository;

import java.sql.SQLException;
import java.util.List;

public class DispatchService {

    private final DispatchRepository dispatchRepository;

    public DispatchService() {
        this.dispatchRepository = new DispatchRepository();
    }

    public void save(Dispatch dispatch) throws SQLException {
        dispatchRepository.save(dispatch);
    }

    public void update(Dispatch dispatch) throws SQLException {
        dispatchRepository.update(dispatch);
    }

    public void delete(int id) throws SQLException {
        dispatchRepository.delete(id);
    }

    public Dispatch findById(int id) throws SQLException {
        return dispatchRepository.findById(id);
    }

    public List<Dispatch> findAll() throws SQLException {
        return dispatchRepository.findAll();
    }
}
