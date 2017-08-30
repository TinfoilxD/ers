package com.revature.gs.ers.data_access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import com.revature.gs.ers.beans.ReimbursementStatus;
import com.revature.gs.ers.exceptions.NoRecordFoundException;

public class ReimbursementStatusDAO implements ReimbursementStatusDAInt {
	
	private static final int PENDING = 1;
	private static final int APPROVED = 2;
	private static final int DENIED = 3;
	
	
	
	private Connection conn;

	public ReimbursementStatusDAO(Connection conn) {
		super();
		this.conn = conn;
	}
	public Set<ReimbursementStatus> getAllStatuses() throws SQLException{
		Set<ReimbursementStatus> statuses = new HashSet<>();
		String sql = "SELECT * FROM ers_reimbursement_status";
		
		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		while(rs.next()){
			ReimbursementStatus newStatus = new ReimbursementStatus();
			newStatus.setId(rs.getInt("reimb_status_id"));
			newStatus.setStatus(rs.getString("reimb_status"));
			statuses.add(newStatus);
		}
		
		return statuses;
	}
	public ReimbursementStatus getStatusFromId(int id) throws SQLException, NoRecordFoundException{
		if(id <= 0)
			throw new IllegalArgumentException();
		ReimbursementStatus status = new ReimbursementStatus();
		
		String sql = "SELECT reimb_status FROM ers_reimbursement_status WHERE "
				+ "reimb_status_id = ?";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setInt(1, id);
		ResultSet rs = stmt.executeQuery();
		
		if(rs.next()){
			status.setId(id);
			status.setStatus(rs.getString("reimb_status"));
		} else{
			throw new NoRecordFoundException(
					String.format("Status for corresponding id %d does not exist", id));
		}
		return status;
	}
	
	//A SQLException is thrown because these methods do not rely on user input
	//Pending/Approved/Denied are seeded database records.
	//Thus, if they cannot be found, the database has a problem.
	@Override
	public ReimbursementStatus getPending() throws SQLException {
		try {
			return getStatusFromId(ReimbursementStatusDAO.PENDING);
		} catch (NoRecordFoundException e) {
			e.printStackTrace();
			throw new SQLException("Pending Status cannot be found in database.");
		}
	}
	@Override
	public ReimbursementStatus getApproved() throws SQLException {
		try {
			return getStatusFromId(ReimbursementStatusDAO.APPROVED);
		} catch (NoRecordFoundException e) {
			e.printStackTrace();
			throw new SQLException("Approved Status cannot be found in database.");
		}
	}
	@Override
	public ReimbursementStatus getDenied() throws SQLException {
		try {
			return getStatusFromId(ReimbursementStatusDAO.DENIED);
		} catch (NoRecordFoundException e) {
			e.printStackTrace();
			throw new SQLException("Denied Status cannot be found in database.");
		}
	}
}
