package com.capitalbank.util.customer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Custom exception class used to represent business logic errors
 * in the application.
 * 
 * This exception is typically thrown when:
 *  - Business rules are violated
 *  - Validation fails
 *  - Application-specific conditions are not met
 * 
 * Example:
 *  - Insufficient balance
 *  - Account is inactive
 *  - Invalid customer data
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    // Log4j2 Logger instance
    private static final Logger logger = LogManager.getLogger(BusinessException.class);

    /**
     * Constructs a new BusinessException with the specified detail message.
     *
     * @param message the detail message explaining the exception
     */
    public BusinessException(String message) {
        super(message);
        logger.error("BusinessException thrown: {}", message);
    }

    /**
     * Constructs a new BusinessException with the specified detail message
     * and cause.
     *
     * @param message the detail message explaining the exception
     * @param cause   the underlying cause of the exception
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        logger.error("BusinessException thrown: {}", message, cause);
    }
}
