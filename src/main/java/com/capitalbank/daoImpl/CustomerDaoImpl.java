package com.capitalbank.daoImpl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.capitalbank.dao.CustomerDao;
import com.capitalbank.dbconfig.DBConnection;
import com.capitalbank.enums.query.CustomerQuery;
import com.capitalbank.model.Customer;
import com.capitalbank.util.customer.ValidationPanUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Implementation of {@link CustomerDao} using JDBC.
 * Provides CRUD operations for Customer entities.
 */
public class CustomerDaoImpl implements CustomerDao {

    // Logger instance for this class
    private static final Logger logger = LogManager.getLogger(CustomerDaoImpl.class);

    // Database connection
    private Connection connection = DBConnection.getConnection();

    /**
     * Saves a new customer to the database.
     *
     * @param customer the customer to save
     * @return true if insertion was successful, false otherwise
     */
    @Override
    public boolean saveCustomer(Customer customer) {
        int idk = 1; // parameter index
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(CustomerQuery.INSERT_CUSTOMER.getQuery())) {

            // Set values in PreparedStatement
            ps.setString(idk++, customer.getFullName());
            setDateOrNull(ps, idk++, customer.getDob());
            ps.setString(idk++, customer.getGender());
            ps.setString(idk++, customer.getAadharNumber());

            String panNumber = customer.getPanNumber();
            if (panNumber != null && ValidationPanUtil.isValidPan(panNumber)) {
                ps.setString(idk++, panNumber);
            } else {
                ps.setString(idk++, panNumber);
            }

            ps.setString(idk++, customer.getEmail());
            ps.setString(idk++, customer.getPassword()); // hash password
            ps.setString(idk++, customer.getPhone());
            ps.setString(idk++, customer.getCity());
            ps.setString(idk++, customer.getState());
            ps.setString(idk++, customer.getAddress());
            ps.setString(idk++, customer.getPincode());
            ps.setString(idk++, customer.getCountry());
            ps.setString(idk++, customer.getRole().name());
            ps.setBoolean(idk, customer.isActive());

            boolean result = ps.executeUpdate() > 0;
            logger.info("Customer saved successfully: {}", customer.getEmail());
            return result;

        } catch (SQLException e) {
            logger.error("Error while saving customer: {}", customer.getEmail(), e);
            throw new RuntimeException("Error while saving the user: " + e.getMessage());
        }
    }

    /**
     * Finds a customer by ID.
     *
     * @param id the customer ID
     * @return an Optional containing the customer if found
     */
    @Override
    public Optional<Customer> findById(long id) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(CustomerQuery.SELECT_BY_ID.getQuery())) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                Optional<Customer> customer = rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
                logger.info("findById({}) returned {}", id, customer.isPresent());
                return customer;
            }

        } catch (Exception e) {
            logger.error("Error retrieving customer by id: {}", id, e);
            throw new RuntimeException("Error while retrieving customer by id: " + e.getMessage());
        }
    }

    /**
     * Finds a customer by email.
     *
     * @param email the customer's email
     * @return an Optional containing the customer if found
     */
    @Override
    public Optional<Customer> findByEmail(String email) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(CustomerQuery.SELECT_BY_EMAIL.getQuery())) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                Optional<Customer> customer = rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
                logger.info("findByEmail({}) returned {}", email, customer.isPresent());
                return customer;
            }

        } catch (Exception e) {
            logger.error("Error retrieving customer by email: {}", email, e);
            throw new RuntimeException("Error while retrieving customer by email: " + e.getMessage());
        }
    }

    /**
     * Returns all customers.
     *
     * @return an Optional containing the list of customers
     */
    @Override
    public Optional<List<Customer>> findAll() {
        List<Customer> customers = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(CustomerQuery.SELECT_ALL_CUSTOMER.getQuery());
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                customers.add(mapRow(rs));
            }

            logger.info("Retrieved {} customers", customers.size());
            return Optional.ofNullable(customers);

        } catch (Exception e) {
            logger.error("Error retrieving all customers", e);
            throw new RuntimeException("Error while retrieving customer: " + e.getMessage());
        }
    }

    /**
     * Updates an existing customer.
     *
     * @param customer the customer to update
     * @return true if update was successful
     */
    @Override
    public boolean updateCustomer(Customer customer) {
        int idk = 1;
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(CustomerQuery.UPDATE_CUSTOMER.getQuery())) {

            ps.setString(idk++, customer.getFullName());
            setDateOrNull(ps, idk++, customer.getDob());
            ps.setString(idk++, customer.getGender());
            ps.setString(idk++, customer.getAadharNumber());
            ps.setString(idk++, customer.getPanNumber());
            ps.setString(idk++, customer.getEmail());
            ps.setString(idk++, customer.getPassword());
            ps.setString(idk++, customer.getPhone());
            ps.setString(idk++, customer.getCity());
            ps.setString(idk++, customer.getState());
            ps.setString(idk++, customer.getAddress());
            ps.setString(idk++, customer.getPincode());
            ps.setString(idk++, customer.getCountry());
            ps.setString(idk++, customer.getRole().name());
            ps.setBoolean(idk++, customer.isActive());
            ps.setLong(idk++, customer.getCustomerId());

            boolean result = ps.executeUpdate() > 0;
            logger.info("Customer updated successfully: {}", customer.getEmail());
            return result;

        } catch (Exception e) {
            logger.error("Error updating customer: {}", customer.getEmail(), e);
            throw new RuntimeException("Error while updating customer: " + e.getMessage());
        }
    }

    /**
     * Updates a customer's password.
     *
     * @param id          the customer ID
     * @param newPassword the new password
     * @return true if update was successful
     */
    @Override
    public boolean updatePassword(long id, String newPassword) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(CustomerQuery.UPDATE_PASSWORD.getQuery())) {

            ps.setString(1, newPassword);
            ps.setLong(2, id);

            boolean result = ps.executeUpdate() > 0;
            logger.info("Password updated for customer id {}", id);
            return result;

        } catch (Exception e) {
            logger.error("Error updating password for customer id {}", id, e);
            throw new RuntimeException("Error while updating password of customer: " + e.getMessage());
        }
    }

    /**
     * Deletes a customer by ID.
     *
     * @param id the customer ID
     * @return true if deletion was successful
     */
    @Override
    public boolean deleteCustomer(long id) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(CustomerQuery.DELETE_CUSTOMER.getQuery())) {

            ps.setLong(1, id);

            boolean result = ps.executeUpdate() > 0;
            logger.info("Customer deleted with id {}", id);
            return result;

        } catch (Exception e) {
            logger.error("Error deleting customer with id {}", id, e);
            throw new RuntimeException("Error while deleting customer: " + e.getMessage());
        }
    }

    /**
     * Maps a ResultSet row to a Customer object.
     *
     * @param rs the ResultSet
     * @return a Customer object
     */
    private Customer mapRow(ResultSet rs) {
        try {
            Customer c = new Customer();
            c.setCustomerId(rs.getLong("customer_id"));
            c.setFullName(rs.getString("full_name"));

            Date dob = rs.getDate("dob");
            if (dob != null) {
                c.setDob(dob.toLocalDate());
            }

            c.setGender(rs.getString("gender"));
            c.setAadharNumber(rs.getString("aadhar_number"));
            c.setPanNumber(rs.getString("pan_number"));
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
            c.setProfileUpdateStatus(rs.getString("profile_update_status"));
            c.setProfileUpdateReason(rs.getString("profile_update_reason"));


            return c;
        } catch (Exception e) {
            logger.error("Error mapping ResultSet to Customer", e);
            return null;
        }
    }

    /**
     * Sets a date parameter or null if the date is null.
     *
     * @param ps    the PreparedStatement
     * @param index parameter index
     * @param date  the LocalDate value
     * @throws SQLException if SQL error occurs
     */
    private void setDateOrNull(PreparedStatement ps, int index, LocalDate date) throws SQLException {
        if (date != null) {
            ps.setDate(index, Date.valueOf(date));
        } else {
            ps.setNull(index, java.sql.Types.DATE);
        }
    }

    /**
     * Returns a database connection.
     *
     * @return Connection object
     */
    private Connection getConnection() {
        return DBConnection.getConnection();
    }

}
