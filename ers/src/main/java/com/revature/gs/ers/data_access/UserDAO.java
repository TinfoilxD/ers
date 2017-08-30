package com.revature.gs.ers.data_access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.revature.gs.ers.beans.User;
import com.revature.gs.ers.exceptions.NoRecordFoundException;
import com.revature.gs.ers.password.PasswordManager;

public class UserDAO implements UserDAInt {
	private Connection conn;

	public UserDAO(Connection conn) {
		super();
		this.conn = conn;
	}

	/**
	 * Method to validate user upon user login. 
	 * Pass in plaintext password
	 * @throws NoRecordFoundException 
	 */
	public User validateUser(String username, String password) throws SQLException, NoRecordFoundException {
		
		if (username == null || password == null
				||username.equals("") || password.equals(""))
			throw new IllegalArgumentException();

		String hashedPassword = PasswordManager.hashPassword(password);
		
		String sql = String.format("SELECT %s, %s, %s, %s, %s, %s FROM ers_users "
				+ "WHERE ers_username = ? AND ers_password = ?",
				"ers_users_id",
				"ers_username",
				"user_first_name",
				"user_last_name",
				"user_email",
				"user_role_id");

		PreparedStatement stmt = conn.prepareStatement(sql);
		
		stmt.setString(1, username);
		stmt.setString(2, hashedPassword);
		
		ResultSet rs = stmt.executeQuery();
		
		
		// checks if resultset is empty or throws an exception
		if (rs.next()) {
			return parseUser(rs);
		} else {
			throw new NoRecordFoundException(
					String.format("Invalid credentials"));
		}
		
	}

	/**
	 * 
	 * @param id
	 * @return instance of User with given id
	 * @throws SQLException
	 * @throws NoRecordFoundException
	 */
	public User getUserFromId(int id) throws SQLException, NoRecordFoundException {
		if (id <= 0)
			throw new IllegalArgumentException();

		String sql = String.format("SELECT %s, %s, %s, %s, %s, %s FROM ers_users "
				+ "WHERE ers_users_id = ?", "ers_username",
				"ers_users_id",
				"user_first_name",
				"user_last_name",
				"user_email",
				"user_role_id");

		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setInt(1, id);

		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			return parseUser(rs);
		} else {
			throw new NoRecordFoundException(
					String.format("User for id %d was not found.", id));
		}
	}


	/**
	 * returns instance of User that matches given username
	 */
	@Override
	public User getUserFromUserName(String username) throws SQLException, NoRecordFoundException {
		if (username == null || username.contentEquals(""))
			throw new IllegalArgumentException();

		String sql = String.format("SELECT %s, %s, %s, %s, %s, %s FROM ers_users "
				+ "WHERE ers_username = ?",
				"ers_users_id",
				"ers_username",
				"user_first_name",
				"user_last_name",
				"user_email",
				"user_role_id");

		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setString(1, username);

		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			return parseUser(rs);
		} else {
			throw new NoRecordFoundException(
					String.format("User for username %s was not found.", username));
		}
	}
	
	/**
	 * Give a resultset with a single record of User and returns user object
	 * @param rs
	 * @return
	 * @throws SQLException
	 * @throws NoRecordFoundException
	 */
	private User parseUser(ResultSet rs) throws SQLException, NoRecordFoundException{
		User user = new User();
		user.setId(rs.getInt("ers_users_id"));
		user.setUsername(rs.getString("ers_username"));
		user.setFirstName(rs.getString("user_first_name"));
		user.setLastName(rs.getString("user_last_name"));
		user.setEmail(rs.getString("user_email"));

		// resolves user_role_id to a user_role object
		UserRoleDAO userRoleDAO = new UserRoleDAO(conn);
		int userRoleId = rs.getInt("user_role_id");
		user.setUserRole(userRoleDAO.getRoleFromId(userRoleId));
		return user;
	}
}
