package com.capitalbank.security;

import java.security.MessageDigest;

public class PasswordUtil {

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes("UTF-8"));
            // Convert bytes to hex
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password: " + e.getMessage(), e);
        }
    }
    
 // Verify password by comparing hashes
    public static boolean verifyPassword(String rawPassword, String storedHash) {
        String hashedInput = hashPassword(rawPassword);
        return hashedInput.equals(storedHash);
    }
}

