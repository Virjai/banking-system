package com.capitalbank.util.table;

import java.sql.Connection;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.capitalbank.dbconfig.DBConnection;

/**
 * Utility class responsible for creating database tables
 * if they do not already exist.
 * 
 * This class handles:
 *  - customers table
 *  - accounts table
 *  - transactions table
 * 
 * Used during application startup or initialization.
 */
public class TableUtil {

    // Log4j2 Logger instance
    private static final Logger logger = LogManager.getLogger(TableUtil.class);
    
    private DBConnection dbConnection;
    
    public void setDbConnection(DBConnection dbConnection) {
    	this.dbConnection = dbConnection;
    }
    
    public void init() {
        createCustomerTableIfNotExists();
        createAccountTableIfNotExists();
        createTransactionTableIfNotExists();
    }


    /**
     * Creates the 'customers' table if it does not already exist.
     */
    public void createCustomerTableIfNotExists() {

        logger.info("Starting creation of customers table (if not exists)");

        String sql = "CREATE TABLE IF NOT EXISTS customers ("
                + " customer_id BIGINT PRIMARY KEY AUTO_INCREMENT,"
                + " full_name VARCHAR(100) NOT NULL,"
                + " dob DATE,"
                + " gender VARCHAR(10),"
                + " aadhar_number VARCHAR(20) UNIQUE,"
                + " pan_number VARCHAR(20) UNIQUE,"
                + " email VARCHAR(100) NOT NULL UNIQUE,"
                + " password VARCHAR(200) NOT NULL,"
                + " phone VARCHAR(15),"
                + " city VARCHAR(100),"
                + " state VARCHAR(100),"
                + " address VARCHAR(255),"
                + " pincode VARCHAR(10),"
                + " country VARCHAR(10),"
                + " role VARCHAR(20) NOT NULL DEFAULT 'USER',"
                + " is_active BOOLEAN DEFAULT TRUE"
                + ")";

        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement()) {

            stmt.execute(sql);
            logger.info("Customers table checked/created successfully");

        } catch (Exception e) {
            logger.error("Error while creating customers table", e);
            throw new RuntimeException("Failed to create customer table", e);
        }
    }

    /**
     * Creates the 'transactions' table if it does not already exist.
     */
    public void createTransactionTableIfNotExists() {

        logger.info("Starting creation of transactions table (if not exists)");

        String sql = "CREATE TABLE IF NOT EXISTS transactions ("
                + " transaction_id BIGINT PRIMARY KEY AUTO_INCREMENT,"
                + " from_account_id BIGINT,"
                + " to_account_id BIGINT,"
                + " amount DECIMAL(15,2),"
                + " transaction_type VARCHAR(20),"
                + " transaction_date TIMESTAMP,"
                + " status VARCHAR(20)"
                + ")";

        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement()) {

            stmt.execute(sql);
            logger.info("Transactions table checked/created successfully");

        } catch (Exception e) {
            logger.error("Error while creating transactions table", e);
            throw new RuntimeException("Failed to create transaction table", e);
        }
    }

    /**
     * Creates the 'accounts' table if it does not already exist.
     */
    public void createAccountTableIfNotExists() {

        logger.info("Starting creation of accounts table (if not exists)");

        String sql = "CREATE TABLE IF NOT EXISTS accounts ("
                + " account_id BIGINT PRIMARY KEY,"
                + " customer_id BIGINT NOT NULL,"
                + " account_number VARCHAR(30) NOT NULL UNIQUE,"
                + " account_type VARCHAR(20) NOT NULL,"
                + " balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,"
                + " created_at TIMESTAMP NOT NULL,"
                + " is_active BOOLEAN NOT NULL DEFAULT TRUE"
                + ")";

        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement()) {

            stmt.execute(sql);
            logger.info("Accounts table checked/created successfully");

        } catch (Exception e) {
            logger.error("Error while creating accounts table", e);
            throw new RuntimeException("Failed to create accounts table", e);
        }
    }

    /**
     * Returns a database connection using DBConnection utility.
     * 
     * @return Connection object
     */
    private Connection getConnection() {
        logger.debug("Fetching database connection");
        return dbConnection.getConnection();
    }
}
