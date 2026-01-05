package com.capitalbank.dbconfig;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * DBConnection as Spring XML bean (no annotations)
 */
public final class DBConnection {

    private String driverName;
    private String dbUrl;
    private String dbUsername;
    private String dbPassword;

    public DBConnection() {
        // Spring will construct the object
    }

    // called by Spring using init-method
    public void init() {
        loadDatabaseConfig();
        loadDriver();
    }

    private void loadDatabaseConfig() {
        Properties properties = new Properties();

        try (InputStream inputStream =
                     DBConnection.class.getClassLoader()
                             .getResourceAsStream("config/db.properties")) {

            if (inputStream == null) {
                throw new IllegalStateException("Configuration file 'config/db.properties' not found.");
            }

            properties.load(inputStream);

            this.driverName = properties.getProperty("DRIVER_NAME");
            this.dbUrl = properties.getProperty("DB_URL");
            this.dbUsername = properties.getProperty("DB_USER");
            this.dbPassword = properties.getProperty("DB_PASSWORD");

            validateDatabaseConfig();

        } catch (IOException e) {
            throw new IllegalStateException("Failed to load database configuration: " + e.getMessage());
        }
    }

    private void validateDatabaseConfig() {
        validateField(this.driverName, "DRIVER_NAME");
        validateField(this.dbUrl, "DB_URL");
        validateField(this.dbUsername, "DB_USERNAME");
        validateField(this.dbPassword, "DB_PASSWORD");
    }

    private void validateField(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException(fieldName + " is missing or empty in db.properties");
        }
    }

    private void loadDriver() {
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("Driver not found: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);

            if (connection == null) {
                throw new SQLException("Failed to create the database connection.");
            }

            return connection;

        } catch (SQLException e) {
            throw new RuntimeException("Database connection failed: " + e.getMessage(), e);
        }
    }

    public void close(AutoCloseable closable) {
        if (closable == null) return;

        try {
            closable.close();
        } catch (Exception e) {
            System.err.println("Failed to close resource: " + e.getMessage());
        }
    }
}
