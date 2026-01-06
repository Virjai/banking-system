package com.capitalbank.util.customer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility class for validating PAN (Permanent Account Number).
 * 
 * This class provides methods to validate Indian PAN numbers
 * using regular expressions.
 * 
 * PAN Format: AAAAA9999A
 * Example: ABCDE1234F
 */
public final class ValidationPanUtil {

    // Log4j2 Logger instance
    private static final Logger logger = LogManager.getLogger(ValidationPanUtil.class);

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class.
     */
    private ValidationPanUtil() {
        // Prevent object creation
    }

    /**
     * Regular expression for validating PAN format.
     * 
     * Format: 5 uppercase letters + 4 digits + 1 uppercase letter
     */
    public static final String PAN_REGEX = "^[A-Z]{5}[0-9]{4}[A-Z]$";

    /**
     * Validates the given PAN number.
     *
     * @param pan the PAN number to validate
     * @return true if PAN is valid, false otherwise
     */
    public static boolean isValidPan(String pan) {

        logger.debug("Validating PAN: {}", pan);

        if (pan == null) {
            logger.warn("PAN validation failed: PAN is null");
            return false;
        }

        boolean isValid = pan.matches(PAN_REGEX);

        if (isValid) {
            logger.info("PAN validation successful: {}", pan);
        } else {
            logger.warn("PAN validation failed for value: {}", pan);
        }

        return isValid;
    }
}
