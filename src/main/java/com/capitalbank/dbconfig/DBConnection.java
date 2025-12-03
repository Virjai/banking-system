package com.capitalbank.dbconfig;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * This class manages the database connection configuration and provides methods
 * to establish and close database connections.
 */
public final class DBConnection {
	private static volatile DBConnection instance = null;

	// Database connection parameters
	private String driverName;
	private String dbUrl;
	private String dbUsername;
	private String dbPassword;

	// Private constructor to prevent instantiation.
	private DBConnection() {
		loadDatabaseConfig();
		loadDriver();
	}

	private static DBConnection getInstance() {
		if (instance == null) {
			synchronized (DBConnection.class) {
				if (instance == null) {
					instance = new DBConnection();
				}
			}
		}
		return instance;
	}

	// Loads the database configuration from the `db.properties` file located in the
	private void loadDatabaseConfig() {
		Properties properties = new Properties();
		try (InputStream inputStream = DBConnection.class.getClassLoader()
				.getResourceAsStream("config/db.properties");) {
			if (inputStream == null) {
				throw new IllegalStateException("Configuration file 'config/db.properties' not found.");
			}
			properties.load(inputStream);

			this.driverName = properties.getProperty("DRIVER_NAME");
			this.dbUrl = properties.getProperty("DB_URL");
			this.dbUsername = properties.getProperty("DB_USER");
			this.dbPassword = properties.getProperty("DB_PASSWORD");

			validateDatabaseConfig();

		} catch (IOException | IllegalStateException e) {
			throw new IllegalStateException("Failed to load database configuration: " + e.getMessage());
		}
	}

	// validating `db.properties file's fields
	private void validateDatabaseConfig() {
		validateField(this.driverName, "DRIVER_NAME");
		validateField(this.dbUrl, "DB_URL");
		validateField(this.dbUsername, "DB_USERNAME");
		validateField(this.dbPassword, "DB_PASSWORD");
	}

	private void validateField(String value, String fieldName) {
		if (value == null || value.isBlank()) {
			throw new IllegalStateException(fieldName + " is missing or empty in db.properties");
		}
	}

	// loading the driver
	private void loadDriver() {
		try {
			Class.forName(driverName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new ExceptionInInitializerError("❌ MySQL Driver not found: " + e.getMessage());
		}
	}

	// Establishes a connection to the database
	public static Connection getConnection() {
		String url = getInstance().dbUrl;
		String username = getInstance().dbUsername;
		String password = getInstance().dbPassword;

		Connection connection = null;
		try {
			connection = DriverManager.getConnection(url, username, password);
			if (connection == null) {
				throw new IllegalArgumentException("Failed to connect database.");
			}
			return connection;
		} catch (SQLException e) {
			throw new RuntimeException("Failed to connect the database: " + e.getMessage());
		}
	}

	// Closes the given resources which is closable
	public static void close(AutoCloseable closable) {
		if (closable == null)
			return;
		try {
			closable.close();
		} catch (Exception e) {
			System.err.println("❌ Failed to close resource: " + e.getMessage());
		}
	}
}
