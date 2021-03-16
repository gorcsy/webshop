package com.codecool.buyourstuff.dao.implementation.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class BaseDaoDb {

    private static final C3P0DataSource dataSourcePool;

    static {
        dataSourcePool = C3P0DataSource.getInstance();
    }

    protected static Connection getConnection() throws SQLException {
        return dataSourcePool.getConnection();
    }

    protected BaseDaoDb() {
        createTable();
    }

    abstract void createTable();

    protected void checkForExecutionSuccess(String sql, String triggerMethod) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);
            System.out.println(triggerMethod + ": OK");
        } catch (SQLException e) {
            throw new RuntimeException(getClass().getSimpleName() + " " + triggerMethod + ": " + e.getSQLState());
        }
    }
}