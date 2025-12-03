package com.capitalbank.util.database;

import java.sql.Connection;
import java.sql.SQLException;

import com.capitalbank.dbconfig.DBConnection;

public class TransactionManager {

	public interface TransactionCallback<T> {
		T execute(Connection connection) throws SQLException;
	}

	public static <T> T doInTransaction(TransactionCallback<T> action) {
		Connection connection = null;

		try {
			connection = DBConnection.getConnection();
			connection.setAutoCommit(false);

			T result = action.execute(connection);

			connection.commit();
			return result;

		} catch (SQLException e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException e1) {
					throw new RuntimeException("Rollback failed: " + e.getMessage());
				}
			}
			throw new RuntimeException("Transaction failed: " + e.getMessage());
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					throw new RuntimeException("Failed to close connection: " + e.getMessage());
				}
			}
		}
	}
}
