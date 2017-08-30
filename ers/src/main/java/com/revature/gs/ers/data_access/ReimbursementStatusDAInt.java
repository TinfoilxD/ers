package com.revature.gs.ers.data_access;

import java.sql.SQLException;
import java.util.Set;

import com.revature.gs.ers.beans.ReimbursementStatus;
import com.revature.gs.ers.exceptions.NoRecordFoundException;

public interface ReimbursementStatusDAInt {
	Set<ReimbursementStatus> getAllStatuses() throws SQLException;
	ReimbursementStatus getStatusFromId(int id) throws SQLException, NoRecordFoundException;
	ReimbursementStatus getPending() throws SQLException;
	ReimbursementStatus getApproved() throws SQLException;
	ReimbursementStatus getDenied() throws SQLException;
	
}
