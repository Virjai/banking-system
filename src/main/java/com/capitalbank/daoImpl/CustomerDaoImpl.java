package com.capitalbank.daoImpl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.capitalbank.dao.CustomerDao;
import com.capitalbank.dbconfig.DBConnection;
import com.capitalbank.enums.query.CustomerQuery;
import com.capitalbank.model.Customer;
import com.capitalbank.util.table.TableUtil;

public class CustomerDaoImpl implements CustomerDao {
	private Connection connection;

	public CustomerDaoImpl() {
		connection = DBConnection.getConnection();
	}
	public CustomerDaoImpl(Connection connection) {
		TableUtil.createCustomerTable();
		this.connection = connection;
	}

	@Override
	public boolean saveCustomer(Customer customer) {
		int idk = 1;
		try (PreparedStatement ps = connection.prepareStatement(CustomerQuery.INSERT_CUSTOMER.getQuery())) {
			ps.setString(idk++, customer.getFullName());
			ps.setDate(idk++, Date.valueOf(customer.getDob()));
			ps.setString(idk++, customer.getGender());
			ps.setString(idk++, customer.getAadharNumber());
			ps.setString(idk++, customer.getPanNumber());
			ps.setString(idk++, customer.getAadharImage());
			ps.setString(idk++, customer.getCustomerImage());
			ps.setString(idk++, customer.getEmail());

			// Hash password
			ps.setString(idk++, customer.getPassword());

			ps.setString(idk++, customer.getPhone());
			ps.setString(idk++, customer.getCity());
			ps.setString(idk++, customer.getState());
			ps.setString(idk++, customer.getAddress());
			ps.setString(idk++, customer.getPincode());
			ps.setString(idk++, customer.getCountry());

			ps.setString(idk++, customer.getRole().name());
			ps.setBoolean(idk++, customer.isActive());

			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException("Error while saving the user: " + e.getMessage());
		}
	}

	@Override
	public Optional<Customer> findById(long id) {
		ResultSet rs = null;

		try (PreparedStatement ps = connection.prepareStatement(CustomerQuery.SELECT_BY_ID.getQuery())) {

			ps.setLong(1, id);
			rs = ps.executeQuery();

			if (rs.next())
				return Optional.ofNullable(mapRow(rs));
			return Optional.empty();

		} catch (Exception e) {
			throw new RuntimeException("Error while retrieving customer by id: " + e.getMessage());
		} finally {
			DBConnection.close(rs);
		}
	}

	@Override
	public Optional<Customer> findByEmail(String email) {
		ResultSet rs = null;

		try (PreparedStatement ps = connection.prepareStatement(CustomerQuery.SELECT_BY_EMAIL.getQuery())) {

			ps.setString(1, email);
			rs = ps.executeQuery();

			if (rs.next())
				return Optional.ofNullable(mapRow(rs));
			return Optional.empty();

		} catch (Exception e) {
			throw new RuntimeException("Error while retrieving customer by email: " + e.getMessage());
		} finally {
			DBConnection.close(rs);
		}
	}

	@Override
	public Optional<List<Customer>> findAll() {
		List<Customer> list = new ArrayList<>();

		try (PreparedStatement ps = connection.prepareStatement(CustomerQuery.SELECT_ALL_CUSTOMER.getQuery())) {
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				list.add(mapRow(rs));
			}

			return Optional.ofNullable(list);
		} catch (Exception e) {
			throw new RuntimeException("Error while retrieving customer: " + e.getMessage());
		}
	}

	@Override
	public boolean updateCustomer(Customer customer) {
		int idk = 1;
		try (PreparedStatement ps = connection.prepareStatement(CustomerQuery.UPDATE_CUSTOMER.getQuery())) {

			ps.setString(idk++, customer.getFullName());
			ps.setDate(idk++, Date.valueOf(customer.getDob()));
			ps.setString(idk++, customer.getGender());
			ps.setString(idk++, customer.getAadharNumber());
			ps.setString(idk++, customer.getPanNumber());
			ps.setString(idk++, customer.getAadharImage());
			ps.setString(idk++, customer.getCustomerImage());
			ps.setString(idk++, customer.getEmail());
			ps.setString(idk++, customer.getPhone());
			ps.setString(idk++, customer.getCity());
			ps.setString(idk++, customer.getState());
			ps.setString(idk++, customer.getAddress());
			ps.setString(idk++, customer.getPincode());
			ps.setString(idk++, customer.getCountry());
			ps.setString(idk++, customer.getRole().name());
			ps.setBoolean(idk++, customer.isActive());

			ps.setLong(idk++, customer.getCustomerId());

			return ps.executeUpdate() > 0;

		} catch (Exception e) {
			throw new RuntimeException("Error while updating customer: " + e.getMessage());
		}
	}

	@Override
	public boolean updatePassword(long id, String newPassword) {
		try (PreparedStatement ps = connection.prepareStatement(CustomerQuery.UPDATE_PASSWORD.getQuery())) {

			ps.setString(1, newPassword);
			ps.setLong(2, id);

			return ps.executeUpdate() > 0;

		} catch (Exception e) {
			throw new RuntimeException("Error while updating password of customer: " + e.getMessage());
		}
	}

	@Override
	public boolean deleteCustomer(long id) {
		try (PreparedStatement ps = connection.prepareStatement(CustomerQuery.DELETE_CUSTOMER.getQuery())) {
			ps.setLong(1, id);

			return ps.executeUpdate() > 0;

		} catch (Exception e) {
			throw new RuntimeException("Error while updating password of customer: " + e.getMessage());
		}
	}

	private Customer mapRow(ResultSet rs) {
		try {
			Customer c = new Customer();

			c.setCustomerId(rs.getLong("customer_id"));
			c.setFullName(rs.getString("full_name"));

			Date dob = rs.getDate("dob");
			if (dob != null)
				c.setDob(dob.toLocalDate());

			c.setGender(rs.getString("gender"));
			c.setAadharNumber(rs.getString("aadhar_number"));
			c.setPanNumber(rs.getString("pan_number"));
			c.setAadharImage(rs.getString("aadhar_image"));
			c.setCustomerImage(rs.getString("customer_image"));

			c.setEmail(rs.getString("email"));
			c.setPassword(rs.getString("password")); 
			c.setPhone(rs.getString("phone"));
			c.setCity(rs.getString("city"));
			c.setState(rs.getString("state"));
			c.setAddress(rs.getString("address"));
			c.setPincode(rs.getString("pincode"));
			c.setCountry(rs.getString("country"));
			c.setRole(Customer.Role.valueOf(rs.getString("role")));

			c.setActive(rs.getBoolean("is_active"));

			return c;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
