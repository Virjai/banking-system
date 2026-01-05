package com.capitalbank.util.table;

import java.sql.Connection;
import java.sql.Statement;


import javax.sql.DataSource;

import com.capitalbank.dbconfig.DBConnection;

public class TableUtil {

//    private final DataSource dataSource;
//
//    public TableUtil(DataSource dataSource) {
//        this.dataSource = dataSource;
//    }
	private Connection connection = DBConnection.getConnection();


    public void createCustomerTableIfNotExists() {

        String sql =
            "CREATE TABLE IF NOT EXISTS customers ("
          + " customer_id BIGINT PRIMARY KEY AUTO_INCREMENT,"
          + " full_name VARCHAR(100) NOT NULL,"
          + " dob DATE,"
          + " gender VARCHAR(10),"
          + " aadhar_number VARCHAR(20) UNIQUE,"
          + " pan_number VARCHAR(20) UNIQUE,"
          + " aadhar_image VARCHAR(255),"
          + " customer_image VARCHAR(255),"
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

        try (
             Statement stmt = connection.createStatement()) {

            stmt.execute(sql);

        } catch (Exception e) {
            throw new RuntimeException("Failed to create customer table", e);
        }
    }
    public void createTransactionTableIfNotExists() {

        String sql =
            "CREATE TABLE IF NOT EXISTS transactions ("
            + "    transaction_id BIGINT PRIMARY KEY AUTO_INCREMENT,"
            + "    from_account_id BIGINT,"
            + "    to_account_id BIGINT,"
            + "    amount DECIMAL(15,2),"
            + "    transaction_type VARCHAR(20),"
            + "    transaction_date TIMESTAMP,"
            + "    status VARCHAR(20)"
            + ")";

        try (
             Statement stmt = connection.createStatement()) {

            stmt.execute(sql);

        } catch (Exception e) {
            throw new RuntimeException("Failed to create transaction table", e);
        }
    }
    
    public void createAccountTableIfNotExists() {

        String sql =
            "CREATE TABLE IF NOT EXISTS accounts ("
            + "    account_id      BIGINT PRIMARY KEY,"
            + "    customer_id     BIGINT NOT NULL,"
            + "    account_number  VARCHAR(30) NOT NULL UNIQUE,"
            + "    account_type    VARCHAR(20) NOT NULL,"
            + "    balance         DECIMAL(15,2) NOT NULL DEFAULT 0.00,"
            + "    created_at      TIMESTAMP NOT NULL,"
            + "    is_active       BOOLEAN NOT NULL DEFAULT TRUE"
            + ")";

        try (
             Statement stmt = connection.createStatement()) {

            stmt.execute(sql);

        } catch (Exception e) {
            throw new RuntimeException("Failed to create accounts table", e);
        }
    }

}
