package com.revature.gs.ers.data_access;

import java.sql.SQLException;

import com.revature.gs.ers.beans.User;
import com.revature.gs.ers.exceptions.NoRecordFoundException;

public interface UserDAInt {
	User validateUser(String username, String password) throws SQLException, NoRecordFoundException;
	User getUserFromId(int id) throws SQLException, NoRecordFoundException;
	User getUserFromUserName(String username) throws SQLException, NoRecordFoundException;
}
