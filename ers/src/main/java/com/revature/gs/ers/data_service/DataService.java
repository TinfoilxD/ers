package com.revature.gs.ers.data_service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

import com.revature.gs.ers.beans.Reimbursement;
import com.revature.gs.ers.beans.ReimbursementStatus;
import com.revature.gs.ers.beans.ReimbursementType;
import com.revature.gs.ers.beans.User;
import com.revature.gs.ers.connection.ConnectionManager;
import com.revature.gs.ers.data_access.ReimbursementDAO;
import com.revature.gs.ers.data_access.ReimbursementStatusDAO;
import com.revature.gs.ers.data_access.ReimbursementTypeDAO;
import com.revature.gs.ers.data_access.UserDAO;
import com.revature.gs.ers.exceptions.NoRecordFoundException;

/**
 * Service layer class for controlling transactions and resources for the data access layer.
 * @author TinfoilxD
 *
 */
public class DataService {

	/*
	 * User Services
	 */
	public static User retrieveUserForLogin(String username, String password) throws SQLException, NoRecordFoundException {
		Connection conn = ConnectionManager.getConnection();
		UserDAO userDAO = new UserDAO(conn);
		User user = userDAO.validateUser(username, password);
		conn.close();
		return user;
	}
	
	/*
	 * Reimbursement Services
	 */
	
	public static Set<Reimbursement> getAllReimbursements() throws SQLException, NoRecordFoundException {
		Connection conn = ConnectionManager.getConnection();
		ReimbursementDAO reimDAO = new ReimbursementDAO(conn);
		Set<Reimbursement> reims = reimDAO.getAllTickets();
		conn.close();
		return reims;
	}
	
	public static Set<Reimbursement> getReimbursementsByStatus(ReimbursementStatus status) throws SQLException, NoRecordFoundException{
		Connection conn = ConnectionManager.getConnection();
		ReimbursementDAO reimDAO = new ReimbursementDAO(conn);
		Set<Reimbursement> reims = reimDAO.getTicketsByStatus(status);
		conn.close();
		return reims;
	}
	
	public static Set<Reimbursement> getReimbursementsForEmployee(User employee) throws SQLException, NoRecordFoundException{
		Connection conn = ConnectionManager.getConnection();
		ReimbursementDAO reimDAO = new ReimbursementDAO(conn);
		Set<Reimbursement> reims = reimDAO.getPastTickets(employee);
		conn.close();
		return reims;
		
	}
	
	public static void processReimbursement(
			Reimbursement reim, ReimbursementStatus status, User resolver) throws SQLException, NoRecordFoundException{
		

		//resolver should exist in the session
		Connection conn = ConnectionManager.getConnection();
		ReimbursementDAO reimDAO = new ReimbursementDAO(conn);
		reimDAO.processRequest(reim, status, resolver);
		conn.close();
	}
	
	public static void insertReimbursement(Reimbursement reim) throws SQLException{
		Connection conn = ConnectionManager.getConnection();
		ReimbursementDAO reimDAO = new ReimbursementDAO(conn);
		if(reim.getStatus() == null){
			//do default status if it doesn't exist
			//could probably be switched over to database to improve performance
			ReimbursementStatus defaultStatus = new ReimbursementStatusDAO(conn).getPending();
			reim.setStatus(defaultStatus);
		}
		reimDAO.insertReimbursement(reim);
		conn.close();
		
	}
	
	/*
	 * ReimbursementStatus services
	 */
	public static ReimbursementStatus getReimbursementStatusFromId(int i) throws SQLException, NoRecordFoundException{
		Connection conn = ConnectionManager.getConnection();
		ReimbursementStatusDAO statusDAO = new ReimbursementStatusDAO(conn);
		ReimbursementStatus status = statusDAO.getStatusFromId(i);
		conn.close();
		return status;
	}

	public static Set<ReimbursementStatus> getAllReimbursementStatuses() throws SQLException{
		Connection conn = ConnectionManager.getConnection();
		ReimbursementStatusDAO statusDAO = new ReimbursementStatusDAO(conn);
		Set<ReimbursementStatus> statuses = statusDAO.getAllStatuses();
		conn.close();
		return statuses;
	}
	
	/*
	 * ReimbursementType services
	 */

	public static ReimbursementType getReimbursementTypeFromId(int i) throws SQLException, NoRecordFoundException{
		Connection conn = ConnectionManager.getConnection();
		ReimbursementTypeDAO typeDAO = new ReimbursementTypeDAO(conn);
		ReimbursementType type = typeDAO.getTypeFromId(i);
		conn.close();
		return type;
	}

	public static Set<ReimbursementType> getAllReimbursementTypes() throws SQLException {
		Connection conn = ConnectionManager.getConnection();
		ReimbursementTypeDAO typeDAO = new ReimbursementTypeDAO(conn);
		Set<ReimbursementType> types = typeDAO.getAllTypes();
		return types;
	}
	
}
