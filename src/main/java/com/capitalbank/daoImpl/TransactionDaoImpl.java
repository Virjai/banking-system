package com.capitalbank.daoImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.capitalbank.dao.TransactionDao;
import com.capitalbank.dbconfig.DBConnection;
import com.capitalbank.model.Transaction;

public class TransactionDaoImpl implements TransactionDao {

	private DBConnection dbConnection;

	public void setDbConnection(DBConnection dbConnection) {
		this.dbConnection = dbConnection;
	}

	private static final String INSERT_SQL = "INSERT INTO transactions (from_account_id, to_account_id, amount, transaction_type, transaction_date, status) "
			+ "VALUES (?, ?, ?, ?, ?, ?)";

	private static final String SELECT_ALL = "SELECT * FROM transactions";

	private static final String SELECT_BY_ID = "SELECT * FROM transactions WHERE transaction_id = ?";

	private static final String SELECT_BY_DATE = "SELECT * FROM transactions WHERE transaction_date BETWEEN ? AND ?";

	private static final String UPDATE_STATUS = "UPDATE transactions SET status = ? WHERE transaction_id = ?";

	private static final String DELETE_BY_ID = "DELETE FROM transactions WHERE transaction_id = ?";

	// ================= CREATE =================
	@Override
	public Transaction save(Transaction transaction) {

		Connection con = null;
		try {
			con = dbConnection.getConnection();
			con.setAutoCommit(false);

			try (PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

				ps.setLong(1, transaction.getFromAccountId());
				ps.setLong(2, transaction.getToAccountId());
				ps.setDouble(3, transaction.getAmount());
				ps.setString(4, transaction.getTransactionType());
				ps.setTimestamp(5, Timestamp.valueOf(transaction.getTransactionDate()));
				ps.setString(6, transaction.getStatus());

				ps.executeUpdate();

				try (ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next()) {
						transaction.setTransactionId(rs.getLong(1));
					}
				}
			}

			con.commit();
			return transaction;

		} catch (Exception e) {
			rollbackQuietly(con);
			throw new RuntimeException("Error saving transaction", e);

		} finally {
			dbConnection.close(con);
		}
	}

	// ================= READ =================
	@Override
	public Optional<List<Transaction>> findAllTransaction() {

		List<Transaction> transactions = new ArrayList<>();
		Connection con = null;

		try {
			con = dbConnection.getConnection();

			try (PreparedStatement ps = con.prepareStatement(SELECT_ALL); ResultSet rs = ps.executeQuery()) {

				while (rs.next()) {
					transactions.add(mapRow(rs));
				}
			}

			return transactions.isEmpty() ? Optional.empty() : Optional.of(transactions);

		} catch (Exception e) {
			throw new RuntimeException("Error fetching transactions", e);

		} finally {
			dbConnection.close(con);
		}
	}

	@Override
	public Optional<Transaction> findByTransactionId(long transactionId) {

		Connection con = null;

		try {
			con = dbConnection.getConnection();

			try (PreparedStatement ps = con.prepareStatement(SELECT_BY_ID)) {
				ps.setLong(1, transactionId);

				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						return Optional.of(mapRow(rs));
					}
				}
			}

			return Optional.empty();

		} catch (Exception e) {
			throw new RuntimeException("Error fetching transaction by ID", e);

		} finally {
			dbConnection.close(con);
		}
	}

	@Override
	public Optional<Transaction> findTransactionByDate(LocalDate start, LocalDate end) {

		Connection con = null;

		try {
			con = dbConnection.getConnection();

			try (PreparedStatement ps = con.prepareStatement(SELECT_BY_DATE)) {

				ps.setTimestamp(1, Timestamp.valueOf(start.atStartOfDay()));
				ps.setTimestamp(2, Timestamp.valueOf(end.atTime(23, 59, 59)));

				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						return Optional.of(mapRow(rs));
					}
				}
			}

			return Optional.empty();

		} catch (Exception e) {
			throw new RuntimeException("Error fetching transaction by date", e);

		} finally {
			dbConnection.close(con);
		}
	}

	// ================= UPDATE =================
	@Override
	public boolean updateByTransactionId(long transactionId) {

		Connection con = null;

		try {
			con = dbConnection.getConnection();
			con.setAutoCommit(false);

			try (PreparedStatement ps = con.prepareStatement(UPDATE_STATUS)) {
				ps.setString(1, "COMPLETED");
				ps.setLong(2, transactionId);

				boolean updated = ps.executeUpdate() > 0;
				con.commit();
				return updated;
			}

		} catch (Exception e) {
			if (con != null) {
				try {
					con.rollback();
				} catch (Exception ex) {
				}
			}
			throw new RuntimeException("Error updating transaction status", e);

		} finally {
			dbConnection.close(con);
		}
	}

	// ================= DELETE =================
	@Override
	public boolean deleteByTransactionId(long transactionId) {

		Connection con = null;

		try {
			con = dbConnection.getConnection();
			con.setAutoCommit(false);

			try (PreparedStatement ps = con.prepareStatement(DELETE_BY_ID)) {
				ps.setLong(1, transactionId);

				boolean deleted = ps.executeUpdate() > 0;
				con.commit();
				return deleted;
			}

		} catch (Exception e) {
			rollbackQuietly(con);
			throw new RuntimeException("Error deleting transaction", e);

		} finally {
			dbConnection.close(con);
		}
	}

	// ================= UTIL =================
	private Transaction mapRow(ResultSet rs) throws SQLException {
		return new Transaction(rs.getLong("transaction_id"), rs.getLong("from_account_id"), rs.getLong("to_account_id"),
				rs.getDouble("amount"), rs.getString("transaction_type"),
				rs.getTimestamp("transaction_date").toLocalDateTime(), rs.getString("status"));
	}

	private void rollbackQuietly(Connection con) {
		if (con != null) {
			try {
				con.rollback();
			} catch (Exception ex) {
				ex.printStackTrace(); // or log
			}
		}
	}
}
