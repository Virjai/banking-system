package com.capitalbank.util.table;

import java.sql.Connection;
import java.sql.Statement;


import javax.sql.DataSource;

public class TableUtil {

    private final DataSource dataSource;

    public TableUtil(DataSource dataSource) {
        this.dataSource = dataSource;
    }

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

        try (Connection con = dataSource.getConnection();
             Statement stmt = con.createStatement()) {

            stmt.execute(sql);

        } catch (Exception e) {
            throw new RuntimeException("Failed to create customer table", e);
        }
    }
}
