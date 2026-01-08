package com.capitalbank.security;

import java.security.MessageDigest;

import org.springframework.security.authentication.encoding.PasswordEncoder;

public class PasswordUtil implements PasswordEncoder {

//    public static String hashPassword(String password) {
//        
//    }
//    
// // Verify password by comparing hashes
//    public static boolean verifyPassword(String rawPassword, String storedHash) {
//        String hashedInput = hashPassword(rawPassword);
//        return hashedInput.equals(storedHash);
//    }

    @Override
    public String encodePassword(String rawPass, Object salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(rawPass.getBytes("UTF-8"));

            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (Exception e) {
            throw new RuntimeException("Error while hashing password", e);
        }
    }

    @Override
    public boolean isPasswordValid(String encPass, String rawPass, Object salt) {
        String hashed = encodePassword(rawPass, salt);
        return encPass.equals(hashed);
    }
}

