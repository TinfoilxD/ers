package com.revature.gs.ers.data_access;

import java.sql.SQLException;
import java.util.Set;

import com.revature.gs.ers.beans.Reimbursement;
import com.revature.gs.ers.beans.ReimbursementStatus;
import com.revature.gs.ers.beans.User;
import com.revature.gs.ers.exceptions.NoRecordFoundException;

public interface ReimbursementDAInt {
	void insertReimbursement(Reimbursement reim) throws SQLException;
	Set<Reimbursement> getAllTickets() throws SQLException, NoRecordFoundException;
	Set<Reimbursement> getPastTickets(User user) throws SQLException, NoRecordFoundException;
	Set<Reimbursement> getTicketsByStatus(ReimbursementStatus status) throws SQLException, NoRecordFoundException;
	void processRequest(Reimbursement reim, ReimbursementStatus status, User resolver) throws SQLException;
	Reimbursement getReimbursementById(int id) throws SQLException, NoRecordFoundException;
	
	
}
