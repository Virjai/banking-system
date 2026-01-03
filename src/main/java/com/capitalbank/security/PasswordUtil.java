package com.capitalbank.security;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
	private static final int BCRYPT_COST_FACTOR = 12;
	
	public static String hashPassword(String password) {
		
		return BCrypt.hashpw(password, BCrypt.gensalt(BCRYPT_COST_FACTOR));
	}
	
	public static boolean validatePassword(String plainPassword, String hashPassword) {
		return BCrypt.checkpw(plainPassword, hashPassword);
	}
}
