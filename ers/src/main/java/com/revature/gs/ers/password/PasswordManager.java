package com.revature.gs.ers.password;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordManager {
	
	private static final String salt = "$2a$12$KmqCAdtcpI1hEiTSK0BFge";
	
	public static String hashPassword(String password){
		return BCrypt.hashpw(password, salt);
	}
}
