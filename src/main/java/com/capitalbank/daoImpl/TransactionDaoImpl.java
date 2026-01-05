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
import com.capitalbank.util.table.TableUtil;

public class TransactionDaoImpl implements TransactionDao {
	
	public TransactionDaoImpl() {
		super();
		new TableUtil().createTransactionTableIfNotExists();
	}
	
	private static final String INSERT_SQL = "INSERT INTO transactions (from_account_id, to_account_id, amount, transaction_type, transaction_date, status) "
			+ "VALUES (?, ?, ?, ?, ?, ?)";

	private static final String SELECT_ALL = "SELECT * FROM transactions";

	private static final String SELECT_BY_ID = "SELECT * FROM transactions WHERE transaction_id = ?";

	private static final String SELECT_BY_DATE = "SELECT * FROM transactions WHERE transaction_date BETWEEN ? AND ?";

	private static final String UPDATE_STATUS = "UPDATE transactions SET status = ? WHERE transaction_id = ?";

	private static final String DELETE_BY_ID = "DELETE FROM transactions WHERE transaction_id = ?";
	
	private Connection connection = DBConnection.getConnection();
	
	@Override
	public Transaction save(Transaction transaction) {

		try (PreparedStatement ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

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
			return transaction;

		} catch (SQLException e) {
			throw new RuntimeException("Error saving transaction", e);
		}
	}

	@Override
	public Optional<List<Transaction>> findAllTransaction() {
		List<Transaction> transactions = new ArrayList<>();

		try (PreparedStatement ps = connection.prepareStatement(SELECT_ALL); ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				transactions.add(mapRow(rs));
			}

			return transactions.isEmpty() ? Optional.empty() : Optional.of(transactions);

		} catch (SQLException e) {
			throw new RuntimeException("Error fetching transactions", e);
		}
	}

	@Override
	public Optional<Transaction> findByTransactionId(long transactionId) {
		try (PreparedStatement ps = connection.prepareStatement(SELECT_BY_ID)) {

			ps.setLong(1, transactionId);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return Optional.of(mapRow(rs));
				}
			}
			return Optional.empty();

		} catch (SQLException e) {
			throw new RuntimeException("Error fetching transaction by ID", e);
		}
	}

	@Override
	public Optional<Transaction> findTransactionByDate(LocalDate initialDate,
			LocalDate endDate) {
		try (PreparedStatement ps = connection.prepareStatement(SELECT_BY_DATE)) {

			ps.setTimestamp(1, Timestamp.valueOf(initialDate.atStartOfDay()));
			ps.setTimestamp(2, Timestamp.valueOf(endDate.atTime(23, 59, 59)));

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return Optional.of(mapRow(rs));
				}
			}
			return Optional.empty();

		} catch (SQLException e) {
			throw new RuntimeException("Error fetching transaction by date", e);
		}
	}

	@Override
	public boolean updateByTransactionId(long transactionId) {
		try (PreparedStatement ps = connection.prepareStatement(UPDATE_STATUS)) {

			ps.setString(1, "COMPLETED");
			ps.setLong(2, transactionId);

			return ps.executeUpdate() > 0;

		} catch (SQLException e) {
			throw new RuntimeException("Error updating transaction status", e);
		}
	}

	@Override
	public boolean deleteByTransactionId(long transactionId) {
		try (PreparedStatement ps = connection.prepareStatement(DELETE_BY_ID)) {

			ps.setLong(1, transactionId);
			return ps.executeUpdate() > 0;

		} catch (SQLException e) {
			throw new RuntimeException("Error deleting transaction", e);
		}
	}

	private Transaction mapRow(ResultSet rs) throws SQLException {
		return new Transaction(
				rs.getLong("transaction_id"), 
				rs.getLong("from_account_id"), 
				rs.getLong("to_account_id"),
				rs.getDouble("amount"), 
				rs.getString("transaction_type"),
				rs.getTimestamp("transaction_date").toLocalDateTime(), 
				rs.getString("status"));
	}

}
