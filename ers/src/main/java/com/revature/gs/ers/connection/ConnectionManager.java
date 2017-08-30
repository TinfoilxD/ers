package com.revature.gs.ers.connection;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionManager {

	private static String url = "jdbc:oracle:thin:@chinook-1707-java.cutzf6y98kpp.us-east-2.rds.amazonaws.com:1521:ORCL";
	private static String user = "ERS";
	private static String password = "magicspacebunnies";

	public static Connection getConnection() {
		try {
			Class.forName("oracle.jdbc.OracleDriver");
			return DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			throw new IllegalStateException("Database cannot be reached.");
		}
	}
}
