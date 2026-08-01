package com.relieftrack.service;

import com.relieftrack.model.ReliefItem;
import com.relieftrack.repository.ReliefItemRepository;

import java.sql.SQLException;
import java.util.List;

public class ReliefItemService {
    private final ReliefItemRepository reliefItemRepository = new ReliefItemRepository();

    public void save(ReliefItem item) throws SQLException { reliefItemRepository.save(item); }
    public void update(ReliefItem item) throws SQLException { reliefItemRepository.update(item); }
    public List<ReliefItem> findAll() throws SQLException { return reliefItemRepository.findAll(); }
}
