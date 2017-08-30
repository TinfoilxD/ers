package com.revature.gs.ers.data_access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import com.revature.gs.ers.beans.UserRole;
import com.revature.gs.ers.exceptions.NoRecordFoundException;

public class UserRoleDAO implements UserRoleDAInt{

	private static final int EMPLOYEE = 1;
	private static final int FINANCE_MANAGER = 2;
	
	private Connection conn;

	public UserRoleDAO(Connection conn) {
		super();
		this.conn = conn;
	}
	
	public Set<UserRole> getAllRoles() throws SQLException{
		Set<UserRole> roles = new HashSet<UserRole>();
		
		String sql = "SELECT * FROM ers_user_roles";
		
		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		
		while(rs.next()){
			UserRole newRole = new UserRole();
			newRole.setId(rs.getInt("ers_user_role_id"));
			newRole.setUserRole(rs.getString("user_role"));
			roles.add(newRole);
		}
		
		return roles;
	}

	@Override
	public UserRole getRoleFromId(int id) throws SQLException, NoRecordFoundException {
		UserRole role = new UserRole();
		
		String sql = "SELECT * FROM ers_user_roles WHERE ers_user_role_id = ?";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setInt(1, id);
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			role.setId(id);
			role.setUserRole(rs.getString("user_role"));
			return role;
		} else{
			throw new NoRecordFoundException(
				String.format("User role of id %d could not be found.",id));
		}
		
	}

	@Override
	public UserRole getEmployeeRole() throws SQLException {
		try{
			return getRoleFromId(UserRoleDAO.EMPLOYEE);
		}catch(NoRecordFoundException e){
			throw new SQLException("Employee user role not found.");
		}
	}

	@Override
	public UserRole getFinanceManagerRole() throws SQLException{
		try{
			return getRoleFromId(UserRoleDAO.FINANCE_MANAGER);
		} catch(NoRecordFoundException e){
			throw new SQLException("FinanceManager role not found.");
		}
	}

}
