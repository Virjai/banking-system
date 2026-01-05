package com.capitalbank.util.customer;

public final class ValidationPanUtil {

    private ValidationPanUtil() {}

    public static final String PAN_REGEX =
            "^[A-Z]{5}[0-9]{4}[A-Z]$";

    public static boolean isValidPan(String pan) {
        return pan != null && pan.matches(PAN_REGEX);
    }
}

