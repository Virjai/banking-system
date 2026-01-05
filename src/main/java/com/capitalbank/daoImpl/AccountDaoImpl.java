package com.capitalbank.daoImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.capitalbank.dao.AccountDao;
import com.capitalbank.dbconfig.DBConnection;
import com.capitalbank.enums.query.AccountQuery;
import com.capitalbank.model.Account;
import com.capitalbank.util.table.TableUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Implementation of {@link AccountDao} using JDBC.
 * Provides CRUD operations for Account entities.
 */
public class AccountDaoImpl implements AccountDao {

    private static final Logger logger = LogManager.getLogger(AccountDaoImpl.class);

    private Connection connection = DBConnection.getConnection();

    /**
     * Initializes the DAO and ensures the account table exists.
     */
    public AccountDaoImpl() {
        new TableUtil().createAccountTableIfNotExists();
    }

    /**
     * Saves a new account in the database.
     *
     * @param account the account to save
     * @return true if insertion was successful, false otherwise
     */
    @Override
    public boolean save(Account account) {
        try (PreparedStatement ps = connection.prepareStatement(AccountQuery.SAVE_ACCOUNT.getQuery())) {

            // Set account parameters
            ps.setLong(1, account.getCustomerId());
            ps.setString(2, account.getAccountNumber());
            ps.setString(3, account.getAccountType());
            ps.setDouble(4, account.getBalance());
            LocalDateTime createdAt = account.getCreatedAt();
            ps.setTimestamp(5, createdAt != null ? Timestamp.valueOf(createdAt) : null);
            ps.setBoolean(6, account.isActive());
            ps.setString(7, account.getGstNumber());

            logger.debug("Executing save account query: {} with parameters: {}", 
                AccountQuery.SAVE_ACCOUNT.getQuery(), account);

            int affectedRows = ps.executeUpdate();
            boolean result = affectedRows > 0;

            if(result) logger.info("Account saved successfully: {}", account.getAccountNumber());
            return result;

        } catch (SQLException e) {
            logger.error("Error saving account: {}", account.getAccountNumber(), e);
            throw new RuntimeException("Error while saving account: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves all accounts.
     *
     * @return Optional containing the list of accounts
     */
    @Override
    public Optional<List<Account>> findAllAccount() {
        List<Account> accounts = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(AccountQuery.SELECT_ALL_ACCOUNTS.getQuery())) {

            while (rs.next()) {
                accounts.add(mapResultSetToAccount(rs));
            }

            logger.info("Retrieved {} accounts from database", accounts.size());
            return accounts.isEmpty() ? Optional.empty() : Optional.of(accounts);

        } catch (Exception e) {
            logger.error("Error retrieving all accounts", e);
            throw new RuntimeException("Error while retrieving all accounts: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves all accounts for a specific customer.
     *
     * @param customerId the customer ID
     * @return Optional containing the list of accounts
     */
    @Override
    public Optional<List<Account>> findAccountByCustomerId(long customerId) {
        List<Account> accounts = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(AccountQuery.SELECT_ALL_ACCOUNT_BY_CUSTOMER_ID.getQuery())) {
            ps.setLong(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    accounts.add(mapResultSetToAccount(rs));
                }
            }

            logger.info("Retrieved {} accounts for customerId={}", accounts.size(), customerId);
            return accounts.isEmpty() ? Optional.empty() : Optional.of(accounts);

        } catch (SQLException e) {
            logger.error("Error retrieving accounts by customerId={}", customerId, e);
            throw new RuntimeException("Error while retrieving accounts by customer id: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves an account by its ID.
     *
     * @param accountId the account ID
     * @return Optional containing the account if found
     */
    @Override
    public Optional<Account> findByAccountId(long accountId) {
        Account account = null;
        try (PreparedStatement ps = connection.prepareStatement(AccountQuery.SELECT_ACCOUNT_BY_ID.getQuery())) {
            ps.setLong(1, accountId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    account = mapResultSetToAccount(rs);
                }
            }

            logger.info("Retrieved account by accountId={}: {}", accountId, account != null);
            return Optional.ofNullable(account);

        } catch (SQLException e) {
            logger.error("Error retrieving account by accountId={}", accountId, e);
            throw new RuntimeException("Error while retrieving account by id: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves an account by its account number.
     *
     * @param accountNumber the account number
     * @return Optional containing the account if found
     */
    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        Account account = null;
        try (PreparedStatement ps = connection.prepareStatement(AccountQuery.SELECT_ACCOUNT_BY_NUMBER.getQuery())) {
            ps.setString(1, accountNumber);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    account = mapResultSetToAccount(rs);
                }
            }

            logger.info("Retrieved account by accountNumber={}: {}", accountNumber, account != null);
            return Optional.ofNullable(account);

        } catch (SQLException e) {
            logger.error("Error retrieving account by accountNumber={}", accountNumber, e);
            throw new RuntimeException("Error while retrieving account by account number: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an account by accountId.
     *
     * @param accountId the account ID
     * @param account   the account details to update
     * @return true if update was successful
     */
    @Override
    public boolean updateByAccountId(long accountId, Account account) {
        Optional<Account> existing = findByAccountId(accountId);
        if (existing.isEmpty()) return false;

        try (PreparedStatement ps = connection.prepareStatement(AccountQuery.UPDATE_BY_ACCOUNT_ID.getQuery())) {
            ps.setString(1, account.getAccountType());
            ps.setDouble(2, account.getBalance());
            ps.setBoolean(3, account.isActive());
            ps.setLong(4, accountId);

            int rows = ps.executeUpdate();
            logger.info("Updated accountId={}: {} row(s) affected", accountId, rows);
            return rows > 0;

        } catch (SQLException e) {
            logger.error("Error updating accountId={}", accountId, e);
            throw new RuntimeException("Error while updating account by id: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an account by account number.
     *
     * @param accountNumber the account number
     * @param account       the account details to update
     * @return true if update was successful
     */
    @Override
    public boolean updateByAccountNumber(String accountNumber, Account account) {
        Optional<Account> existing = findByAccountNumber(accountNumber);
        if (existing.isEmpty()) return false;

        try (PreparedStatement ps = connection.prepareStatement(AccountQuery.UPDATE_BY_ACCOUNT_NUMBER.getQuery())) {
            ps.setString(1, account.getAccountType());
            ps.setDouble(2, account.getBalance());
            ps.setBoolean(3, account.isActive());
            ps.setString(4, accountNumber);

            int rows = ps.executeUpdate();
            logger.info("Updated accountNumber={}: {} row(s) affected", accountNumber, rows);
            return rows > 0;

        } catch (SQLException e) {
            logger.error("Error updating accountNumber={}", accountNumber, e);
            throw new RuntimeException("Error while updating account by account number: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes an account by accountId.
     *
     * @param accountId the account ID
     * @return true if deletion was successful
     */
    @Override
    public boolean deleteByAccountId(long accountId) {
        Optional<Account> existing = findByAccountId(accountId);
        if (existing.isEmpty()) return false;

        try (PreparedStatement ps = connection.prepareStatement(AccountQuery.DELETE_BY_ACCOUNT_ID.getQuery())) {
            ps.setLong(1, accountId);

            int rows = ps.executeUpdate();
            logger.info("Deleted accountId={}: {} row(s) affected", accountId, rows);
            return rows > 0;

        } catch (SQLException e) {
            logger.error("Error deleting accountId={}", accountId, e);
            throw new RuntimeException("Error while deleting account by id: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes an account by account number.
     *
     * @param accountNumber the account number
     * @return true if deletion was successful
     */
    @Override
    public boolean deleteByAccountNumber(String accountNumber) {
        Optional<Account> existing = findByAccountNumber(accountNumber);
        if (existing.isEmpty()) return false;

        try (PreparedStatement ps = connection.prepareStatement(AccountQuery.DELETE_BY_ACCOUNT_NUMBER.getQuery())) {
            ps.setString(1, accountNumber);

            int rows = ps.executeUpdate();
            logger.info("Deleted accountNumber={}: {} row(s) affected", accountNumber, rows);
            return rows > 0;

        } catch (SQLException e) {
            logger.error("Error deleting accountNumber={}", accountNumber, e);
            throw new RuntimeException("Error while deleting account by account number: " + e.getMessage(), e);
        }
    }

    /**
     * Finds an account by GST number.
     *
     * @param gstNumber the GST number
     * @return Optional containing the account if found
     */
    @Override
    public Optional<Account> findByGstNumber(String gstNumber) {
        if (gstNumber == null || gstNumber.isBlank()) return Optional.empty();

        Account account = null;
        try (PreparedStatement ps = connection.prepareStatement(AccountQuery.SELECT_ACCOUNT_BY_GST.getQuery())) {
            ps.setString(1, gstNumber);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    account = mapResultSetToAccount(rs);
                }
            }

            logger.info("Retrieved account by GST number={}: {}", gstNumber, account != null);
            return Optional.ofNullable(account);

        } catch (SQLException e) {
            logger.error("Error retrieving account by GST number={}", gstNumber, e);
            throw new RuntimeException("Error while retrieving account by GST number: " + e.getMessage(), e);
        }
    }

    /**
     * Maps a ResultSet row to an Account object.
     *
     * @param rs the ResultSet
     * @return mapped Account object
     * @throws SQLException if SQL error occurs
     */
    private Account mapResultSetToAccount(ResultSet rs) throws SQLException {
        Account account = new Account();
        account.setAccountId(rs.getLong("account_id"));
        account.setCustomerId(rs.getLong("customer_id"));
        account.setAccountNumber(rs.getString("account_number"));
        account.setAccountType(rs.getString("account_type"));
        account.setBalance(rs.getDouble("balance"));

        Timestamp timestamp = rs.getTimestamp("created_at");
        account.setCreatedAt(timestamp != null ? timestamp.toLocalDateTime() : null);
        account.setActive(rs.getBoolean("is_active"));
        account.setGstNumber(rs.getString("gst_number"));

        return account;
    }
}
