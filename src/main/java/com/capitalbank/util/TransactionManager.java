package com.capitalbank.util;

import java.sql.Connection;
import java.sql.SQLException;

import org.zkoss.zkplus.spring.SpringUtil;

import com.capitalbank.dbconfig.DBConnection;

public class TransactionManager {

	public interface TransactionCallback<T> {
		T execute(Connection connection) throws SQLException;
	}

	public static <T> T doInTransaction(TransactionCallback<T> action) {
		Connection connection = null;

		try {
			connection =  (Connection) SpringUtil.getBean("dbConnection");
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

	@FunctionalInterface
	public interface VoidTransactionCallback {
		void execute(Connection connection) throws SQLException;
	}

	public static void doInTransaction(VoidTransactionCallback action) {
		Connection connection = null;

		try {
			connection = (Connection) SpringUtil.getBean("dbConnection");;
			connection.setAutoCommit(false);

			action.execute(connection);

			connection.commit();

		} catch (SQLException e) {
			rollbackQuietly(connection);
			throw new RuntimeException("Transaction failed", e);
		} finally {
			closeQuietly(connection);
		}
	}

	private static void rollbackQuietly(Connection connection) {
		if (connection != null) {
			try {
				connection.rollback();
			} catch (SQLException e) {
				throw new RuntimeException("Rollback failed", e);
			}
		}
	}

	private static void closeQuietly(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new RuntimeException("Failed to close connection", e);
			}
		}
	}

}