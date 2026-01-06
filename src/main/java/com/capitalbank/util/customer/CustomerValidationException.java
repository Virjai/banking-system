package com.capitalbank.util.customer;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Custom exception class used to represent validation errors
 * related to Customer data.
 * 
 * This exception is thrown when one or more validation rules fail
 * during customer data verification.
 * 
 * It can hold:
 *  - A single error message
 *  - Multiple validation error messages combined into one
 */
public class CustomerValidationException extends RuntimeException {

    private static final long serialVersionUID = 8018839726759526752L;

    // Log4j2 Logger instance
    private static final Logger logger = LogManager.getLogger(CustomerValidationException.class);

    /**
     * Constructs a new CustomerValidationException with a single error message.
     *
     * @param message the validation error message
     */
    public CustomerValidationException(String message) {
        super(message);
        logger.warn("Customer validation exception thrown: {}", message);
    }

    /**
     * Constructs a new CustomerValidationException with multiple validation messages.
     * The messages are joined into a single string.
     *
     * @param messages list of validation error messages
     */
    public CustomerValidationException(List<String> messages) {
        super(String.join(", ", messages));
        logger.warn("Customer validation exception thrown with multiple errors: {}", messages);
    }
}
