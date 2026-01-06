package com.capitalbank.util.customer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.capitalbank.model.Customer;

/**
 * Utility class responsible for validating Customer objects.
 * 
 * This class checks mandatory fields, format rules, and business constraints
 * before allowing customer data to be processed or persisted.
 * 
 * If validation fails, a CustomerValidationException is thrown
 * containing all validation error messages.
 */
public class CustomerValidator {

    // Log4j2 Logger instance
    private static final Logger logger = LogManager.getLogger(CustomerValidator.class);

    /**
     * Validates the given Customer object.
     *
     * @param customer the Customer object to validate
     * @throws CustomerValidationException if validation fails
     */
    public static void validate(Customer customer) {

        logger.info("Starting validation for Customer");

        List<String> errors = new ArrayList<>();

        // Null check for customer object
        if (customer == null) {
            logger.error("Customer validation failed: Customer object is null");
            throw new CustomerValidationException("Customer object is null.");
        }

        // Validate full name
        if (isBlank(customer.getFullName())) {
            errors.add("Customer name is required.");
        }

        // Validate date of birth
        if (customer.getDob() == null) {
            errors.add("Date of birth is required.");
        } else if (customer.getDob().isAfter(LocalDate.now())) {
            errors.add("Date of birth cannot be in the future.");
        }

        // Validate gender
        if (isBlank(customer.getGender())) {
            errors.add("Gender is required.");
        }

        // Validate email
        if (isBlank(customer.getEmail()) || !customer.getEmail().contains("@")) {
            errors.add("Invalid email format.");
        }

        // Validate phone number (must be 10 digits)
        if (isBlank(customer.getPhone()) || customer.getPhone().length() != 10) {
            errors.add("Phone number must be exactly 10 digits.");
        }

        // Validate address
        if (isBlank(customer.getAddress())) {
            errors.add("Address is required.");
        }

        // Validate pincode (must be 6 digits)
        if (isBlank(customer.getPincode()) || customer.getPincode().length() != 6) {
            errors.add("Pincode is required and must be 6 digits only.");
        }

        // Validate city
        if (isBlank(customer.getCity())) {
            errors.add("City is required.");
        }

        // If any validation errors exist, throw exception
        if (!errors.isEmpty()) {
            logger.warn("Customer validation failed with errors: {}", errors);
            throw new CustomerValidationException(errors);
        }

        logger.info("Customer validation successful");
    }

    /**
     * Checks if a string is null, empty, or contains only whitespace.
     *
     * @param str the string to check
     * @return true if blank, false otherwise
     */
    private static boolean isBlank(String str) {
        return str == null || str.trim().isBlank();
    }
}
