package com.capitalbank.daoImpl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.capitalbank.dao.CustomerDao;
import com.capitalbank.dbconfig.DBConnection;
import com.capitalbank.enums.query.CustomerQuery;
import com.capitalbank.model.Account;
import com.capitalbank.model.Customer;
import com.capitalbank.util.PasswordUtil;
import com.capitalbank.util.database.TransactionManager;

public class CustomerDaoImpl implements CustomerDao {

	@Override
	public boolean save(Connection connection, Customer customer) {

		try (PreparedStatement preparedStatement = connection
				.prepareStatement(CustomerQuery.INSERT_CUSTOMER.getQuery());) {

			preparedStatement.setString(1, customer.getCustomerName());

			LocalDate dob = customer.getDob();
			preparedStatement.setDate(2, dob != null ? Date.valueOf(dob) : null);

			preparedStatement.setString(3, customer.getGender());
			preparedStatement.setString(4, customer.getAadhar());
			preparedStatement.setString(5, customer.getAadharImage());
			preparedStatement.setString(6, customer.getCustomerImage());
			preparedStatement.setString(7, customer.getEmail());
			preparedStatement.setString(8, PasswordUtil.hashPassword(customer.getPassword()));
			preparedStatement.setString(9, customer.getPhone());
			preparedStatement.setString(10, customer.getAddress());

			int affectedRows = preparedStatement.executeUpdate();
			return affectedRows > 0;
		} catch (SQLException e) {
			throw new RuntimeException("Error saving user: " + e.getMessage());
		}
	}

	@Override
	public Optional<List<Customer>> findAllCustomer(Connection connection) {
		Map<Long, Customer> map = new LinkedHashMap<>();

		try (Statement statement = connection.createStatement();) {

			ResultSet resultSet = statement.executeQuery(CustomerQuery.SELECT_ALL_CUSTOMERS.getQuery());
			while (resultSet.next()) {
				long id = resultSet.getLong("customerId");
				Customer customer = map.computeIfAbsent(id, (key) -> extractCustomer(resultSet));
				Account account = extractAccount(resultSet);

				if (account != null) {
					customer.getAccountList().add(account);
				}
			}

			List<Customer> customers = new ArrayList<>(map.values());
			return customers.isEmpty() ? Optional.empty() : Optional.of(customers);

		} catch (SQLException e) {
			throw new RuntimeException("Error retrieving customer: " + e.getMessage());
		}
	}

	@Override
	public Optional<Customer> findByCustomerId(Connection connection, long customerId) {
		Customer customer = null;

		try (PreparedStatement preparedStatement = connection
				.prepareStatement(CustomerQuery.SELECT_CUSTOMER_BY_ID.getQuery());) {

			preparedStatement.setLong(1, customerId);
			ResultSet resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				System.out.println("hi");
				if (customer == null) {
					customer = extractCustomer(resultSet);
				}
				Account account = extractAccount(resultSet);
				if (account != null) {
					customer.getAccountList().add(account);
				}
			} else {
				throw new IllegalArgumentException("Invalid customer id.");
			}

		} catch (SQLException e) {
			throw new RuntimeException("Failed to find customer by id: " + e.getMessage());
		}
		return Optional.ofNullable(customer);
	}

	@Override
	public Optional<Customer> findByEmail(Connection connection, String email) {
		Customer customer = null;
		if (!email.contains("@")) {
			throw new IllegalArgumentException("Invalid email.");
		}

		try (PreparedStatement preparedStatement = connection
				.prepareStatement(CustomerQuery.SELECT_CUSTOMER_BY_EMAIL.getQuery());) {
			preparedStatement.setString(1, email.trim());

			ResultSet resultSet = preparedStatement.executeQuery();
			if (!resultSet.next()) {
				throw new RuntimeException("Email does not exist.");
			}

			if (customer == null) {
				customer = extractCustomer(resultSet);
			}
			while (resultSet.next()) {

				Account account = extractAccount(resultSet);
				if (account != null) {
					customer.getAccountList().add(account);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Error retrieving customer by email: " + e.getMessage());
		}
		return Optional.ofNullable(customer);
	}

	@Override
	public Optional<Customer> findByPhone(Connection connection, String phone) {
		Customer customer = null;

		try (PreparedStatement preparedStatement = connection
				.prepareStatement(CustomerQuery.SELECT_CUSTOMER_BY_PHONE.getQuery());) {
			preparedStatement.setString(1, phone);

			ResultSet resultSet = preparedStatement.executeQuery();

			if (!resultSet.next()) {
				throw new IllegalArgumentException("Phone does not exist.");
			}
			if (customer == null) {
				customer = extractCustomer(resultSet);
			}

			while (resultSet.next()) {
				Account account = extractAccount(resultSet);
				if (account != null) {
					customer.getAccountList().add(account);
				}
			}

		} catch (SQLException e) {
			throw new RuntimeException("Error retrieving customer by phone: " + e.getMessage());
		}
		return Optional.ofNullable(customer);
	}

	@Override
	public Optional<Customer> findByAadhar(Connection connection, String aadhar) {
		Customer customer = null;

		try (PreparedStatement preparedStatement = connection
				.prepareStatement(CustomerQuery.SELECT_CUSTOMER_BY_AADHAR.getQuery());) {
			preparedStatement.setString(1, aadhar);

			ResultSet resultSet = preparedStatement.executeQuery();

			if (!resultSet.next()) {
				throw new IllegalArgumentException("Aadhar does not exist.");
			}
			if (customer == null) {
				customer = extractCustomer(resultSet);
			}

			while (resultSet.next()) {
				Account account = extractAccount(resultSet);
				if (account != null) {
					customer.getAccountList().add(account);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Error retrieving customer by aadhar: " + e.getMessage());
		}
		return Optional.ofNullable(customer);
	}

	@Override
	public boolean updateById(Connection connection, long customerId, Customer customer) {

		Optional<Customer> customers = findByCustomerId(connection, customerId);
		if (!customers.isPresent()) {
			throw new RuntimeException("User not found.");
		}

		try (PreparedStatement preparedStatement = connection
				.prepareStatement(CustomerQuery.UPDATE_BY_CUSTOMER_ID.getQuery());) {

			preparedStatement.setString(1, customer.getCustomerName());
			LocalDate dob = customer.getDob();
			preparedStatement.setDate(2, dob != null ? Date.valueOf(dob) : null);
			preparedStatement.setString(3, customer.getGender());
			preparedStatement.setString(4, customer.getAadharImage());
			preparedStatement.setString(5, customer.getCustomerImage());
			preparedStatement.setString(6, customer.getEmail());
			preparedStatement.setString(7, customer.getPassword());
			preparedStatement.setString(8, customer.getPhone());
			preparedStatement.setString(9, customer.getAddress());
			preparedStatement.setLong(10, customerId);

			int affectedRows = preparedStatement.executeUpdate();

			return affectedRows > 0;
		} catch (SQLException e) {
			throw new RuntimeException("Error while updating user by id: " + e.getMessage());
		}
	}

	@Override
	public boolean updateByEmail(Connection connection, String email, Customer customer) {

		Optional<Customer> customers = findByEmail(connection, email);
		if (!customers.isPresent()) {
			throw new RuntimeException("User not found.");
		}

		try (PreparedStatement preparedStatement = connection
				.prepareStatement(CustomerQuery.UPDATE_BY_EMAIL.getQuery());) {

			preparedStatement.setString(1, customer.getCustomerName());
			LocalDate dob = customer.getDob();
			preparedStatement.setDate(2, dob != null ? Date.valueOf(dob) : null);
			preparedStatement.setString(3, customer.getGender());
			preparedStatement.setString(4, customer.getAadharImage());
			preparedStatement.setString(5, customer.getCustomerImage());
			preparedStatement.setString(6, customer.getPassword());
			preparedStatement.setString(7, customer.getPhone());
			preparedStatement.setString(8, customer.getAddress());
			preparedStatement.setString(9, email);

			int affectedRows = preparedStatement.executeUpdate();
			return affectedRows > 0;
		} catch (SQLException e) {
			throw new RuntimeException("Error updating user by email: " + e.getMessage());
		}
	}

	@Override
	public boolean updateByAadhar(Connection connection, String aadhar, Customer customer) {

		Optional<Customer> customers = findByAadhar(connection, aadhar);
		if (!customers.isPresent()) {
			throw new RuntimeException("User not found.");
		}

		try (PreparedStatement preparedStatement = connection
				.prepareStatement(CustomerQuery.UPDATE_BY_AADHAR.getQuery());) {

			preparedStatement.setString(1, customer.getCustomerName());
			LocalDate dob = customer.getDob();
			preparedStatement.setDate(2, dob != null ? Date.valueOf(dob) : null);
			preparedStatement.setString(3, customer.getGender());
			preparedStatement.setString(4, customer.getAadharImage());
			preparedStatement.setString(5, customer.getCustomerImage());
			preparedStatement.setString(6, customer.getEmail());
			preparedStatement.setString(7, customer.getPassword());
			preparedStatement.setString(8, customer.getPhone());
			preparedStatement.setString(9, customer.getAddress());
			preparedStatement.setString(10, aadhar);

			int affectedRows = preparedStatement.executeUpdate();
			return affectedRows > 0;
		} catch (SQLException e) {
			throw new RuntimeException("Error while updating user by aadhar: " + e.getMessage());
		}
	}

	@Override
	public boolean deleteById(Connection connection, long customerId) {

		Optional<Customer> customers = findByCustomerId(connection, customerId);
		if (customers.isEmpty()) {
			throw new RuntimeException("User not found");
		}

		try (PreparedStatement preparedStatement = connection
				.prepareStatement(CustomerQuery.DELETE_BY_CUSTOMER_ID.getQuery());) {

			preparedStatement.setLong(1, customerId);
			int affectedRows = preparedStatement.executeUpdate();

			connection.commit();
			return affectedRows > 0;
		} catch (SQLException e) {
			throw new RuntimeException("Error while deleting user by id: " + e.getMessage());
		}
	}

	@Override
	public boolean deleteByEmail(Connection connection, String email) {

		Optional<Customer> customers = findByEmail(connection, email);
		if (!customers.isPresent()) {
			throw new RuntimeException("User not found");
		}

		try (PreparedStatement preparedStatement = connection
				.prepareStatement(CustomerQuery.DELETE_BY_EMAIL.getQuery());) {

			preparedStatement.setString(1, email);
			int affectedRows = preparedStatement.executeUpdate();

			return affectedRows > 0;
		} catch (SQLException e) {
			throw new RuntimeException("Error while deleting customer by email: " + e.getMessage());
		}

	}

	@Override
	public boolean deleteByAadhar(Connection connection, String aadhar) {

		Optional<Customer> customers = findByAadhar(connection, aadhar);
		if (!customers.isPresent()) {
			throw new RuntimeException("User not found");
		}

		try (PreparedStatement preparedStatement = connection
				.prepareStatement(CustomerQuery.DELETE_BY_AADHAR.getQuery());) {

			preparedStatement.setString(1, aadhar);
			int affectedRows = preparedStatement.executeUpdate();
			return affectedRows > 0;
		} catch (SQLException e) {
			throw new RuntimeException("Error deleting user by aadhar: " + e.getMessage());
		}
	}

	/**
	 * ***************** Helper function ******************
	 */

	private Customer extractCustomer(ResultSet resultSet) {

		try {
			Customer customer = new Customer();
			customer.setCustomerId(resultSet.getLong("customerId"));
			customer.setCustomerName(resultSet.getString("customerName"));
			Date dob = resultSet.getDate("dob");

			customer.setDob(dob != null ? dob.toLocalDate() : null);
			customer.setGender(resultSet.getString("gender"));
			customer.setAadhar(resultSet.getString("aadhar"));
			customer.setAadharImage(resultSet.getString("aadharImage"));
			customer.setCustomerImage(resultSet.getString("customerImage"));
			customer.setEmail(resultSet.getString("email"));
			customer.setPassword(resultSet.getString("password"));
			customer.setPhone(resultSet.getString("phone"));
			customer.setAddress(resultSet.getString("address"));

			customer.setAccountList(new ArrayList<>());
			return customer;

		} catch (Exception e) {
			throw new RuntimeException("Error extracting customer: " + e.getMessage());
		}
	}

	private Account extractAccount(ResultSet resultSet) {
		System.out.println("Bye");
		try {
			long accountId = resultSet.getLong("accountId");
			if (accountId == 0) {
				return null;
			}
			Account account = new Account();
			account.setAccountId(accountId);
			account.setBalance(resultSet.getDouble("balance"));
			account.setAccountType(resultSet.getString("accountType"));
			Timestamp timestamp = resultSet.getTimestamp("createdAt");
			if (timestamp != null) {
				account.setCreatedAt(timestamp.toLocalDateTime());
			}
			return account;
		} catch (SQLException e) {
			throw new RuntimeException("Error extracting account: " + e.getMessage());
		}
	}
}
