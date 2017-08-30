package com.revature.gs.ers.data_access;

import java.sql.SQLException;
import java.util.Set;

import com.revature.gs.ers.beans.UserRole;
import com.revature.gs.ers.exceptions.NoRecordFoundException;

public interface UserRoleDAInt {
	Set<UserRole> getAllRoles() throws SQLException;
	UserRole getRoleFromId(int id) throws SQLException, NoRecordFoundException;
	UserRole getEmployeeRole() throws SQLException;
	UserRole getFinanceManagerRole() throws SQLException;
	
}
