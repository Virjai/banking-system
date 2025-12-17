package com.capitalbank.util.table;

import java.sql.Connection;
import java.sql.Statement;

import com.capitalbank.dbconfig.DBConnection;

public class TableUtil {
	private static Connection connection = DBConnection.getConnection();

	public static boolean createCustomerTable() {
		String query = """
					CREATE TABLE if not exists customers (
					    customer_id      BIGINT PRIMARY KEY AUTO_INCREMENT,

					    -- Identification
					    full_name        VARCHAR(100) NOT NULL,

					    -- Personal Information
					    dob              DATE NOT NULL,
					    gender           VARCHAR(10) NOT NULL,
					    aadhar_number    VARCHAR(20) NOT NULL UNIQUE,
					    pan_number       VARCHAR(20) UNIQUE,
					    aadhar_image     VARCHAR(255),
					    customer_image   VARCHAR(255),

					    -- Contact Information
					    email            VARCHAR(100) NOT NULL UNIQUE,
					    password         VARCHAR(200) NOT NULL,
					    mobile           VARCHAR(15),
					    city             VARCHAR(100),
					    state            VARCHAR(100),
					    address          VARCHAR(255),
					    pincode          VARCHAR(10),
					    country			 VARCHAR(10),

					    -- Role (Enum stored as String)
					     role             VARCHAR(20) NOT NULL DEFAULT 'USER'
				                  CHECK (role IN ('USER', 'ADMIN')),

					    -- Active flag
					    is_active        BOOLEAN NOT NULL DEFAULT TRUE
					);

				""";

		try (Statement statement = connection.createStatement();) {
			return statement.executeUpdate(query) > 0;
		} catch (Exception e) {
			throw new RuntimeException("Error while creating table: " + e.getMessage());
		}
	}
}
