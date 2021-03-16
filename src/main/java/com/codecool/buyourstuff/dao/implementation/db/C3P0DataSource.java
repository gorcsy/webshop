package com.codecool.buyourstuff.dao.implementation.db;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class C3P0DataSource {
    private static C3P0DataSource dataSource;

    private final String conStr = "jdbc:postgresql://localhost:5432/";
    private final String dbName = System.getenv("PSQL_DB_NAME");
    private final String userName = System.getenv("PSQL_USER_NAME");
    private final String password = System.getenv("PSQL_PASSWORD");

    private ComboPooledDataSource comboPooledDataSource;

    private C3P0DataSource() {
        createDbIfNotExists();
        createConnectionPool();
    }

    private void createDbIfNotExists() {
        try (Connection con = DriverManager.getConnection(conStr + userName, userName, password);
             Statement stmt = con.createStatement()) {

            stmt.execute("CREATE DATABASE " + dbName);

            System.out.printf("Database %s created successfully.%n", dbName);
        } catch (SQLException ex) {
            System.err.printf("ERROR: database %s already exists%n", dbName);
        }
    }

    private void createConnectionPool() {
        try {
            comboPooledDataSource = new ComboPooledDataSource();
            comboPooledDataSource.setDriverClass("org.postgresql.Driver");
            comboPooledDataSource.setJdbcUrl(conStr + dbName);
            comboPooledDataSource.setUser(userName);
            comboPooledDataSource.setPassword(password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static C3P0DataSource getInstance() {
        if (dataSource == null) {
            dataSource = new C3P0DataSource();
        }
        return dataSource;
    }

    public Connection getConnection() {
        Connection con = null;
        try {
            con = comboPooledDataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }
}
