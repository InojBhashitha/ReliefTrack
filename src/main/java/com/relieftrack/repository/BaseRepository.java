package com.relieftrack.repository;

import com.relieftrack.database.DatabaseManager;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class BaseRepository {

    protected Connection getConnection() throws SQLException {
        return DatabaseManager.getConnection();
    }

}