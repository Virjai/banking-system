package com.capitalbank.daoImpl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;


import com.capitalbank.dao.AccountDao;
import com.capitalbank.enums.query.AccountQuery;
import com.capitalbank.model.Account;

public class AccountDaoImpl implements AccountDao {
	private DataSource dataSource;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	@Override
	public boolean save(Account account) {

		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(AccountQuery.SAVE_ACCOUNT.getQuery());) {

			preparedStatement.setLong(1, account.getCustomerId());
			preparedStatement.setString(2, account.getAccountNumber());
			preparedStatement.setString(3, account.getAccountType());
			preparedStatement.setDouble(4, account.getBalance());

			LocalDateTime createdAt = account.getCreatedAt();
			preparedStatement.setTimestamp(5, createdAt != null ? Timestamp.valueOf(createdAt) : null);
			preparedStatement.setBoolean(6, account.isActive());

			int affectedRows = preparedStatement.executeUpdate();
			return affectedRows > 0;

		} catch (SQLException e) {
			throw new RuntimeException("Error while saving account: " + e.getMessage());
		}
	}

	@Override
	public Optional<List<Account>> findAllAccount() {
		List<Account> accounts = new ArrayList<>();

		try (Connection connection = dataSource.getConnection();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(AccountQuery.SELECT_ALL_ACCOUNTS.getQuery())) {

			while (resultSet.next()) {
				Account account = new Account();

				account.setAccountId(resultSet.getLong("accountId"));
				account.setCustomerId(resultSet.getLong("customerId"));
				account.setAccountNumber(resultSet.getString("accountNumber"));
				account.setAccountType(resultSet.getString("accountType"));
				account.setBalance(resultSet.getDouble("balance"));

				Timestamp timestamp = resultSet.getTimestamp("createdAt");
				account.setCreatedAt(timestamp != null ? timestamp.toLocalDateTime() : null);
				account.setActive(resultSet.getBoolean("isActive"));

				accounts.add(account);
			}
		} catch (Exception e) {
			throw new RuntimeException("Error while retrieving all accounts: " + e.getMessage());
		}
		return accounts.isEmpty() ? Optional.empty() : Optional.of(accounts);
	}

	@Override
	public Optional<List<Account>> findAccountByCustomerId(long customerId) {
		List<Account> accounts = new ArrayList<>();

		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection
				.prepareStatement(AccountQuery.SELECT_ALL_ACCOUNT_BY_CUSTOMER_ID.getQuery())) {
			preparedStatement.setLong(1, customerId);

			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Account account = new Account();
				account.setAccountId(resultSet.getLong("accountId"));
				account.setAccountNumber(resultSet.getString("accountNumber"));
				account.setAccountType(resultSet.getString("accountType"));
				account.setBalance(resultSet.getDouble("balance"));

				Timestamp timestamp = resultSet.getTimestamp("createdAt");
				account.setCreatedAt(timestamp != null ? timestamp.toLocalDateTime() : null);

				account.setActive(resultSet.getBoolean("isActive"));
				accounts.add(account);
			}

		} catch (SQLException e) {
			throw new RuntimeException("Error while retrieving accounts by customer id: " + e.getMessage());
		}
		return accounts.isEmpty() ? Optional.empty() : Optional.of(accounts);
	}

	@Override
	public Optional<Account> findByAccountId(long accountId) {

		Account account = null;
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection
				.prepareStatement(AccountQuery.SELECT_ACCOUNT_BY_ID.getQuery())) {

			preparedStatement.setLong(1, accountId);

			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {

				account = new Account();
				account.setCustomerId(resultSet.getLong("customerId"));
				account.setAccountId(resultSet.getLong("accountId"));
				account.setAccountNumber(resultSet.getString("accountNumber"));
				account.setAccountType(resultSet.getString("accountType"));
				account.setBalance(resultSet.getDouble("balance"));

				Timestamp timestamp = resultSet.getTimestamp("createdAt");
				account.setCreatedAt(timestamp != null ? timestamp.toLocalDateTime() : null);
				account.setActive(resultSet.getBoolean("isActive"));
			}

			return Optional.ofNullable(account);
		} catch (SQLException e) {
			throw new RuntimeException("Error while retrieving account by id: " + e.getMessage());
		}
	}

	@Override
	public Optional<Account> findByAccountNumber(String accountNumber) {
		Account account = null;
		ResultSet resultSet = null;
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection
				.prepareStatement(AccountQuery.SELECT_ACCOUNT_BY_NUMBER.getQuery())) {
			preparedStatement.setString(1, accountNumber);

			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				account = new Account();
				account.setAccountId(resultSet.getLong("accountId"));
				account.setCustomerId(resultSet.getLong("customerId"));
				account.setAccountNumber(resultSet.getString("accountNumber"));
				account.setAccountType(resultSet.getString("accountType"));
				account.setBalance(resultSet.getDouble("balance"));

				Timestamp timestamp = resultSet.getTimestamp("createdAt");
				account.setCreatedAt(timestamp != null ? timestamp.toLocalDateTime() : null);
				account.setActive(resultSet.getBoolean("isActive"));
			}

			return Optional.ofNullable(account);

		} catch (SQLException e) {
			throw new RuntimeException("Error while retrieving account by account number: " + e.getMessage());
		}
	}

	@Override
	public boolean updateByAccountId(long accountId, Account account) {

		Optional<Account> existingAccount = findByAccountId(accountId);
		if (!existingAccount.isPresent()) {
			return false;
		}

		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection
				.prepareStatement(AccountQuery.UPDATE_BY_ACCOUNT_ID.getQuery())) {

			preparedStatement.setString(1, account.getAccountType());
			preparedStatement.setDouble(2, account.getBalance());
			preparedStatement.setBoolean(3, account.isActive());
			preparedStatement.setLong(4, accountId);

			return preparedStatement.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException("Error while updating account by id: " + e.getMessage());
		}
	}

	@Override
	public boolean updateByAccountNumber(String accountNumber, Account account) {

		// Find the existing account by account number
		Optional<Account> existingAccount = findByAccountNumber(accountNumber);
		if (existingAccount.isEmpty()) {
			// Return empty if the account doesn't exist
			return false;
		}

		Account updatedAccount = null;
		// Prepare the update statement
		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection
				.prepareStatement(AccountQuery.UPDATE_BY_ACCOUNT_NUMBER.getQuery())) {

			preparedStatement.setString(1, account.getAccountType());
			preparedStatement.setDouble(2, account.getBalance());
			preparedStatement.setBoolean(3, account.isActive());
			preparedStatement.setString(4, accountNumber);

			// Execute the update (using executeUpdate() for UPDATE, not executeQuery())
			int rowsUpdated = preparedStatement.executeUpdate();
			if (rowsUpdated > 0) {
				connection.commit();

				// Retrieve the updated account details
				updatedAccount = findByAccountNumber(accountNumber).orElse(null);
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			throw new RuntimeException("Error while updating account", e);
		}
	}

	@Override
	public boolean deleteByAccountId(long accountId) {

		Optional<Account> existingAccount = findByAccountId(accountId);
		if (!existingAccount.isPresent()) {
			return false;
		}

		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection
				.prepareStatement(AccountQuery.DELETE_BY_ACCOUNT_ID.getQuery())) {
			preparedStatement.setLong(1, accountId);

			if (preparedStatement.executeUpdate() > 0) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			throw new RuntimeException("Error while deleting account by id: " + e.getMessage());
		}
	}

	@Override
	public boolean deleteByAccountNumber(String accountNumber) {

		Optional<Account> existingAccount = findByAccountNumber(accountNumber);
		if (existingAccount.isEmpty()) {
			return false;
		}

		try (Connection connection = dataSource.getConnection();
				PreparedStatement preparedStatement = connection
				.prepareStatement(AccountQuery.DELETE_BY_ACCOUNT_NUMBER.getQuery())) {

			preparedStatement.setString(1, accountNumber);

			if (preparedStatement.executeUpdate() > 0) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			throw new RuntimeException("Error while deleting account by account number: " + e.getMessage());
		}
	}
}
