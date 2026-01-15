package com.capitalbank.dbconfig;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * DBConnection is a singleton utility class responsible for: - Loading database
 * configuration from db.properties - Loading JDBC driver - Providing database
 * connections - Safely closing database resources
 *
 * This class is used across the application to manage DB connectivity.
 */
public final class DBConnection {

	// Log4j2 Logger instance
	private static final Logger logger = LogManager.getLogger(DBConnection.class);

	// Database connection parameters
	private String driverName;
	private String dbUrl;
	private String dbUsername;
	private String dbPassword;

	/**
	 * Private constructor to prevent external instantiation. Loads DB configuration
	 * and JDBC driver.
	 */
	public DBConnection() {
		logger.info("DBConnection bean created");
	}

	// This will be called by Spring at startup
	public void init() {
		logger.info("Initializing DBConnection...");
		loadDatabaseConfig();
		loadDriver();
	}

	/**
	 * Loads database configuration from config/db.properties file.
	 */
	private void loadDatabaseConfig() {
		logger.info("Loading database configuration from db.properties");

		Properties properties = new Properties();

		try (InputStream inputStream = DBConnection.class.getClassLoader()
				.getResourceAsStream("config/db.properties")) {

			if (inputStream == null) {
				logger.error("config/db.properties file not found in classpath");
				throw new IllegalStateException("Configuration file 'config/db.properties' not found.");
			}

			properties.load(inputStream);

			this.driverName = properties.getProperty("DRIVER_NAME");
			this.dbUrl = properties.getProperty("DB_URL");
			this.dbUsername = properties.getProperty("DB_USER");
			this.dbPassword = properties.getProperty("DB_PASSWORD");

			validateDatabaseConfig();

			logger.info("Database configuration loaded successfully");

		} catch (IOException | IllegalStateException e) {
			logger.error("Failed to load database configuration", e);
			throw new IllegalStateException("Failed to load database configuration: " + e.getMessage(), e);
		}
	}

	/**
	 * Validates that all required database properties are present.
	 */
	private void validateDatabaseConfig() {
		logger.debug("Validating database configuration fields");

		validateField(this.driverName, "DRIVER_NAME");
		validateField(this.dbUrl, "DB_URL");
		validateField(this.dbUsername, "DB_USER");
		validateField(this.dbPassword, "DB_PASSWORD");

		logger.info("Database configuration validation successful");
	}

	/**
	 * Validates a single configuration field.
	 *
	 * @param value     the field value
	 * @param fieldName the name of the field
	 */
	private void validateField(String value, String fieldName) {
		if (value == null || value.isBlank()) {
			logger.error("{} is missing or empty in db.properties", fieldName);
			throw new IllegalStateException(fieldName + " is missing or empty in db.properties");
		}
	}

	/**
	 * Loads the JDBC driver class.
	 */
	private void loadDriver() {
		logger.info("Loading JDBC driver: {}", driverName);

		try {
			Class.forName(driverName);
			logger.info("JDBC driver loaded successfully");
		} catch (ClassNotFoundException e) {
			logger.fatal("JDBC Driver not found: {}", driverName, e);
			throw new ExceptionInInitializerError("❌ JDBC Driver not found: " + e.getMessage());
		}
	}

	// 🔑 THIS is what your DAO will use
	public Connection getConnection() {
		try {
			return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
		} catch (Exception e) {
			logger.error("Failed to create DB connection", e);
			throw new RuntimeException("Failed to create DB connection", e);
		}
	}

	/**
	 * Closes any AutoCloseable resource safely.
	 *
	 * @param closable the resource to close (Connection, Statement, ResultSet,
	 *                 etc.)
	 */
	public static void close(AutoCloseable closable) {

		if (closable == null) {
			return;
		}

		try {
			closable.close();
			logger.debug("Resource closed successfully: {}", closable.getClass().getSimpleName());
		} catch (Exception e) {
			logger.warn("Failed to close resource: {}", closable.getClass().getSimpleName(), e);
		}
	}
}
